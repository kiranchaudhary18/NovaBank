package com.novabank.backend.service.impl;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.exception.InvalidAccountStatusException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.TransactionRepository;
import com.novabank.backend.service.BalanceService;
import com.novabank.backend.service.ReceiptService;
import com.novabank.backend.service.TransactionService;
import com.novabank.backend.util.ReferenceNumberGenerator;
import com.novabank.backend.util.TransactionIdGenerator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing deposit, withdrawal, and search operations.
 * Implements validations and auditing.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;
    private final ReceiptService receiptService;
    private final com.novabank.backend.service.EventPublisherService eventPublisherService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionReceiptResponse deposit(User user, DepositRequest request) {
        log.info("Processing cash deposit request for account {}", request.getAccountNumber());
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + request.getAccountNumber()));

        // Verification 1: Enforce active account status
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStatusException("Deposit declined: Account is not ACTIVE. Current status: " + account.getStatus());
        }

        BigDecimal opening = account.getBalance();

        // Perform balance credit adjustment
        balanceService.deposit(account, request.getAmount());
        Account savedAccount = accountRepository.save(account);

        // Save transaction record
        Transaction transaction = Transaction.builder()
                .transactionId(TransactionIdGenerator.generateId())
                .referenceNumber(ReferenceNumberGenerator.generateReference())
                .receiverAccount(savedAccount)
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .openingBalance(opening)
                .closingBalance(savedAccount.getBalance())
                .currency(savedAccount.getCurrency())
                .remarks(request.getRemarks() != null ? request.getRemarks() : "Cash Deposit")
                .status(TransactionStatus.SUCCESS)
                .initiatedBy(user.getEmail())
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction savedTxn = transactionRepository.save(transaction);
        log.info("Deposit successful. Reference: {}", savedTxn.getReferenceNumber());
        eventPublisherService.publishTransactionCompletedEvent(savedTxn);
        return receiptService.generateReceipt(savedTxn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionReceiptResponse withdraw(User user, WithdrawRequest request) {
        log.info("Processing cash withdrawal request for account {}", request.getAccountNumber());
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + request.getAccountNumber()));

        // Verification 1: Enforce active account status
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStatusException("Withdrawal declined: Account is not ACTIVE. Current status: " + account.getStatus());
        }

        // Security check: Customer can only withdraw from their own account
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            if (!account.getCustomer().getUser().getId().equals(user.getId())) {
                throw new ForbiddenException("Access Denied: You do not own the debiting account.");
            }
        }

        // Verification 2: Verify single and daily cash withdrawal limits
        balanceService.verifyLimits(account, request.getAmount(), TransactionType.WITHDRAW);

        BigDecimal opening = account.getBalance();

        // Perform balance debit adjustment
        balanceService.withdraw(account, request.getAmount());
        Account savedAccount = accountRepository.save(account);

        // Save transaction record
        Transaction transaction = Transaction.builder()
                .transactionId(TransactionIdGenerator.generateId())
                .referenceNumber(ReferenceNumberGenerator.generateReference())
                .senderAccount(savedAccount)
                .transactionType(TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .openingBalance(opening)
                .closingBalance(savedAccount.getBalance())
                .currency(savedAccount.getCurrency())
                .remarks(request.getRemarks() != null ? request.getRemarks() : "Cash Withdrawal")
                .status(TransactionStatus.SUCCESS)
                .initiatedBy(user.getEmail())
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction savedTxn = transactionRepository.save(transaction);
        log.info("Withdrawal successful. Reference: {}", savedTxn.getReferenceNumber());
        eventPublisherService.publishTransactionCompletedEvent(savedTxn);
        return receiptService.generateReceipt(savedTxn);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return convertToTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByReference(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference number: " + referenceNumber));
        return convertToTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getTransactionsByAccount(String accountNumber, int page, int size, String sortBy, String sortDir) {
        log.info("Fetching transactions for account number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> pageResult = transactionRepository.findBySenderAccountOrReceiverAccount(account, account, pageable);
        return new PagedResponse<>(pageResult.map(this::convertToTransactionResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> searchTransactions(
            int page, int size, String sortBy, String sortDir,
            String referenceNumber, String transactionId,
            String senderAccountNumber, String receiverAccountNumber,
            BigDecimal minAmount, BigDecimal maxAmount,
            TransactionStatus status, TransactionType type,
            LocalDateTime start, LocalDateTime end
    ) {
        log.info("Searching transactions dynamically - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (referenceNumber != null && !referenceNumber.isBlank()) {
                predicates.add(cb.equal(root.get("referenceNumber"), referenceNumber));
            }
            if (transactionId != null && !transactionId.isBlank()) {
                predicates.add(cb.equal(root.get("transactionId"), transactionId));
            }
            if (senderAccountNumber != null && !senderAccountNumber.isBlank()) {
                predicates.add(cb.equal(root.get("senderAccount").get("accountNumber"), senderAccountNumber));
            }
            if (receiverAccountNumber != null && !receiverAccountNumber.isBlank()) {
                predicates.add(cb.equal(root.get("receiverAccount").get("accountNumber"), receiverAccountNumber));
            }
            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("transactionType"), type));
            }
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), start));
            }
            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Transaction> pageResult = transactionRepository.findAll(spec, pageable);
        return new PagedResponse<>(pageResult.map(this::convertToTransactionResponse));
    }

    @Override
    public TransactionResponse convertToTransactionResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        String senderNum = (transaction.getSenderAccount() != null)
                ? transaction.getSenderAccount().getAccountNumber()
                : null;

        String receiverNum = (transaction.getReceiverAccount() != null)
                ? transaction.getReceiverAccount().getAccountNumber()
                : null;

        String beneficiaryNameStr = (transaction.getBeneficiary() != null)
                ? transaction.getBeneficiary().getBeneficiaryName()
                : null;

        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .referenceNumber(transaction.getReferenceNumber())
                .senderAccountNumber(senderNum)
                .receiverAccountNumber(receiverNum)
                .beneficiaryName(beneficiaryNameStr)
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .openingBalance(transaction.getOpeningBalance())
                .closingBalance(transaction.getClosingBalance())
                .currency(transaction.getCurrency())
                .remarks(transaction.getRemarks())
                .status(transaction.getStatus())
                .failureReason(transaction.getFailureReason())
                .initiatedBy(transaction.getInitiatedBy())
                .approvedBy(transaction.getApprovedBy())
                .transactionDate(transaction.getTransactionDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
