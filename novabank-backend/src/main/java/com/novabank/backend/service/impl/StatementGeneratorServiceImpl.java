package com.novabank.backend.service.impl;

import com.novabank.backend.dto.MiniStatementResponse;
import com.novabank.backend.dto.StatementResponse;
import com.novabank.backend.dto.StatementSummary;
import com.novabank.backend.dto.TransactionHistoryResponse;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.repository.TransactionRepository;
import com.novabank.backend.service.StatementGeneratorService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service implementation managing calculation logic for Statements and Mini Statements.
 * Enforces business dates validations.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatementGeneratorServiceImpl implements StatementGeneratorService {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public StatementResponse generateStatement(Account account, LocalDate startDate, LocalDate endDate) {
        log.info("Generating statement for account: {} from {} to {}", account.getAccountNumber(), startDate, endDate);

        // Validation 1: Start date cannot be after end date
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date.");
        }

        // Validation 2: Future dates are not allowed
        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Statement dates cannot be in the future.");
        }

        // Validation 3: Maximum statement range check (1 year limit)
        long rangeDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (rangeDays > 365) {
            throw new BadRequestException("Statement date range cannot exceed 365 days (1 year).");
        }

        // Fetch transactions chronologically (ascending by date)
        Specification<Transaction> spec = (root, query, cb) -> {
            Predicate isSender = cb.equal(root.get("senderAccount"), account);
            Predicate isReceiver = cb.equal(root.get("receiverAccount"), account);
            Predicate inRange = cb.between(root.get("transactionDate"),
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay()); // Include end date entirely
            return cb.and(cb.or(isSender, isReceiver), inRange);
        };

        List<Transaction> transactions = transactionRepository.findAll(spec, Sort.by("transactionDate").ascending());

        BigDecimal openingBalance = account.getBalance();
        BigDecimal closingBalance = account.getBalance();
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        List<TransactionHistoryResponse> historyRows = new ArrayList<>();

        if (!transactions.isEmpty()) {
            // Opening balance is the openingBalance parameter of the first chronological transaction
            Transaction firstTxn = transactions.get(0);
            if (firstTxn.getSenderAccount() != null && firstTxn.getSenderAccount().getId().equals(account.getId())) {
                openingBalance = firstTxn.getOpeningBalance();
            } else if (firstTxn.getReceiverAccount() != null && firstTxn.getReceiverAccount().getId().equals(account.getId())) {
                openingBalance = firstTxn.getOpeningBalance();
            }

            // Closing balance is the closingBalance parameter of the last chronological transaction
            Transaction lastTxn = transactions.get(transactions.size() - 1);
            if (lastTxn.getSenderAccount() != null && lastTxn.getSenderAccount().getId().equals(account.getId())) {
                closingBalance = lastTxn.getClosingBalance();
            } else if (lastTxn.getReceiverAccount() != null && lastTxn.getReceiverAccount().getId().equals(account.getId())) {
                closingBalance = lastTxn.getClosingBalance();
            }
        }

        for (Transaction txn : transactions) {
            BigDecimal debit = null;
            BigDecimal credit = null;
            BigDecimal running = BigDecimal.ZERO;

            if (txn.getSenderAccount() != null && txn.getSenderAccount().getId().equals(account.getId())) {
                debit = txn.getAmount();
                totalDebits = totalDebits.add(txn.getAmount());
                running = txn.getClosingBalance();
            } else if (txn.getReceiverAccount() != null && txn.getReceiverAccount().getId().equals(account.getId())) {
                credit = txn.getAmount();
                totalCredits = totalCredits.add(txn.getAmount());
                running = txn.getClosingBalance();
            }

            historyRows.add(TransactionHistoryResponse.builder()
                    .id(txn.getId())
                    .transactionId(txn.getTransactionId())
                    .referenceNumber(txn.getReferenceNumber())
                    .transactionDate(txn.getTransactionDate())
                    .transactionType(txn.getTransactionType())
                    .debitAmount(debit)
                    .creditAmount(credit)
                    .runningBalance(running)
                    .remarks(txn.getRemarks())
                    .build());
        }

        String customerName = account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName();
        String periodStr = startDate + " to " + endDate;

        StatementSummary summary = StatementSummary.builder()
                .customerName(customerName)
                .accountNumber(account.getAccountNumber())
                .period(periodStr)
                .openingBalance(openingBalance)
                .closingBalance(closingBalance)
                .totalDebits(totalDebits)
                .totalCredits(totalCredits)
                .build();

        return StatementResponse.builder()
                .summary(summary)
                .transactions(historyRows)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MiniStatementResponse generateMiniStatement(Account account) {
        log.info("Generating Mini Statement for account: {}", account.getAccountNumber());

        // Fetch last 10 transactions descending by date
        Pageable pageable = PageRequest.of(0, 10, Sort.by("transactionDate").descending());
        Page<Transaction> pageResult = transactionRepository.findBySenderAccountOrReceiverAccount(account, account, pageable);
        List<Transaction> descendingTxns = pageResult.getContent();

        // Reverse to sort chronologically (ascending) for statement layout rendering
        List<Transaction> chronologicalTxns = new ArrayList<>(descendingTxns);
        Collections.reverse(chronologicalTxns);

        List<TransactionHistoryResponse> historyRows = new ArrayList<>();
        for (Transaction txn : chronologicalTxns) {
            BigDecimal debit = null;
            BigDecimal credit = null;
            BigDecimal running = BigDecimal.ZERO;

            if (txn.getSenderAccount() != null && txn.getSenderAccount().getId().equals(account.getId())) {
                debit = txn.getAmount();
                running = txn.getClosingBalance();
            } else if (txn.getReceiverAccount() != null && txn.getReceiverAccount().getId().equals(account.getId())) {
                credit = txn.getAmount();
                running = txn.getClosingBalance();
            }

            historyRows.add(TransactionHistoryResponse.builder()
                    .id(txn.getId())
                    .transactionId(txn.getTransactionId())
                    .referenceNumber(txn.getReferenceNumber())
                    .transactionDate(txn.getTransactionDate())
                    .transactionType(txn.getTransactionType())
                    .debitAmount(debit)
                    .creditAmount(credit)
                    .runningBalance(running)
                    .remarks(txn.getRemarks())
                    .build());
        }

        String customerName = account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName();

        return MiniStatementResponse.builder()
                .accountNumber(account.getAccountNumber())
                .customerName(customerName)
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .transactions(historyRows)
                .build();
    }
}
