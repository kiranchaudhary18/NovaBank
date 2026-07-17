package com.novabank.backend.service.impl;

import com.novabank.backend.dto.TransactionReceiptResponse;
import com.novabank.backend.dto.TransferRequest;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Beneficiary;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.BeneficiaryStatus;
import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.exception.InvalidAccountStatusException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.BeneficiaryRepository;
import com.novabank.backend.repository.TransactionRepository;
import com.novabank.backend.service.BalanceService;
import com.novabank.backend.service.ReceiptService;
import com.novabank.backend.service.TransferService;
import com.novabank.backend.util.ReferenceNumberGenerator;
import com.novabank.backend.util.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service implementation managing account fund transfer workflows.
 * Enforces business rules and concurrency controls.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;
    private final ReceiptService receiptService;
    private final com.novabank.backend.service.EventPublisherService eventPublisherService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionReceiptResponse transferFunds(User user, TransferRequest request) {
        log.info("Initiating fund transfer transaction from {} to {}", request.getSenderAccountNumber(), request.getReceiverAccountNumber());

        Account sender = accountRepository.findByAccountNumber(request.getSenderAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found with account number: " + request.getSenderAccountNumber()));

        Account receiver = accountRepository.findByAccountNumber(request.getReceiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found with account number: " + request.getReceiverAccountNumber()));

        // Validation 1: Sender cannot be the same as Receiver
        if (sender.getAccountNumber().equals(receiver.getAccountNumber())) {
            throw new BadRequestException("Sender and receiver accounts must be different.");
        }

        // Security check: Customers can only debit their own accounts
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            if (!sender.getCustomer().getUser().getId().equals(user.getId())) {
                throw new ForbiddenException("Access Denied: You do not own the debiting account.");
            }
        }

        // Validation 2: Enforce active account status checks
        if (sender.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStatusException("Debit declined: Sender account is not ACTIVE. Current status: " + sender.getStatus());
        }
        if (receiver.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStatusException("Credit declined: Receiver account is not ACTIVE. Current status: " + receiver.getStatus());
        }

        // Validation 3: Saved Beneficiary checks if ID is supplied
        Beneficiary beneficiary = null;
        if (request.getBeneficiaryId() != null) {
            beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Saved beneficiary profile not found."));

            if (beneficiary.getStatus() == BeneficiaryStatus.BLOCKED) {
                throw new BadRequestException("Transfer declined: The saved beneficiary is blocked.");
            }

            if (!beneficiary.getBeneficiaryAccountNumber().equals(request.getReceiverAccountNumber())) {
                throw new BadRequestException("Transfer declined: The receiver account number does not match saved beneficiary details.");
            }
        }

        // Decide transaction type
        TransactionType txnType = TransactionType.TRANSFER;
        if (request.getBeneficiaryId() != null) {
            txnType = TransactionType.BENEFICIARY_TRANSFER;
        } else if (sender.getCustomer().getId().equals(receiver.getCustomer().getId())) {
            txnType = TransactionType.INTERNAL_TRANSFER;
        }

        // Validation 4: Verify single and daily limits checks
        balanceService.verifyLimits(sender, request.getAmount(), txnType);

        BigDecimal senderOpening = sender.getBalance();
        BigDecimal receiverOpening = receiver.getBalance();

        // Perform balance adjustments
        balanceService.withdraw(sender, request.getAmount());
        balanceService.deposit(receiver, request.getAmount());

        // Save state changes (protected by Version optimistic locking checks)
        Account updatedSender = accountRepository.save(sender);
        Account updatedReceiver = accountRepository.save(receiver);

        // Generate identifiers
        String referenceNumber = ReferenceNumberGenerator.generateReference();
        String transactionIdBase = TransactionIdGenerator.generateId();

        // Create Debit Transaction Record
        Transaction debitTxn = Transaction.builder()
                .transactionId(transactionIdBase + "D")
                .referenceNumber(referenceNumber)
                .senderAccount(updatedSender)
                .receiverAccount(updatedReceiver)
                .beneficiary(beneficiary)
                .transactionType(txnType)
                .amount(request.getAmount())
                .openingBalance(senderOpening)
                .closingBalance(updatedSender.getBalance())
                .currency(updatedSender.getCurrency())
                .remarks(request.getRemarks())
                .status(TransactionStatus.SUCCESS)
                .initiatedBy(user.getEmail())
                .transactionDate(LocalDateTime.now())
                .build();

        // Create Credit Transaction Record
        Transaction creditTxn = Transaction.builder()
                .transactionId(transactionIdBase + "C")
                .referenceNumber(referenceNumber)
                .senderAccount(updatedSender)
                .receiverAccount(updatedReceiver)
                .beneficiary(beneficiary)
                .transactionType(txnType)
                .amount(request.getAmount())
                .openingBalance(receiverOpening)
                .closingBalance(updatedReceiver.getBalance())
                .currency(updatedReceiver.getCurrency())
                .remarks(request.getRemarks())
                .status(TransactionStatus.SUCCESS)
                .initiatedBy(user.getEmail())
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction savedDebit = transactionRepository.save(debitTxn);
        transactionRepository.save(creditTxn);

        log.info("Atomic fund transfer completed. Reference: {}", referenceNumber);
        
        // Publish event to trigger async debit & credit notifications
        eventPublisherService.publishTransactionCompletedEvent(savedDebit);

        return receiptService.generateReceipt(savedDebit);
    }
}
