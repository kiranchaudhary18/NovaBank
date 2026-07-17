package com.novabank.backend.service.impl;

import com.novabank.backend.entity.Account;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.exception.InsufficientBalanceException;
import com.novabank.backend.exception.MinimumBalanceViolationException;
import com.novabank.backend.exception.TransferLimitExceededException;
import com.novabank.backend.repository.TransactionRepository;
import com.novabank.backend.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service implementation for managing account balances and limit checks.
 * Enforces transaction rules and limits.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    // Configurable Banking limits constants
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");
    private static final BigDecimal SINGLE_TRANSACTION_LIMIT = new BigDecimal("5000.00");
    private static final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("10000.00");
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("2000.00");
    private static final BigDecimal MAXIMUM_DEPOSIT_LIMIT = new BigDecimal("50000.00");

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void deposit(Account account, BigDecimal amount) {
        log.info("Processing balance credit of {} on account {}", amount, account.getAccountNumber());

        if (amount.compareTo(MAXIMUM_DEPOSIT_LIMIT) > 0) {
            throw new TransferLimitExceededException("Deposit amount exceeds the maximum single deposit limit of " + MAXIMUM_DEPOSIT_LIMIT);
        }

        account.setBalance(account.getBalance().add(amount));
        account.setAvailableBalance(account.getAvailableBalance().add(amount));
    }

    @Override
    @Transactional
    public void withdraw(Account account, BigDecimal amount) {
        log.info("Processing balance debit of {} on account {}", amount, account.getAccountNumber());

        if (amount.compareTo(SINGLE_TRANSACTION_LIMIT) > 0) {
            throw new TransferLimitExceededException("Withdrawal amount exceeds the single transaction limit of " + SINGLE_TRANSACTION_LIMIT);
        }

        // Available balance check
        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient available funds for transaction.");
        }

        // Minimum balance constraint check
        BigDecimal resultingBalance = account.getBalance().subtract(amount);
        if (resultingBalance.compareTo(MINIMUM_BALANCE) < 0) {
            throw new MinimumBalanceViolationException("Transaction declined: Resulting balance falls below the minimum required balance of " + MINIMUM_BALANCE);
        }

        account.setBalance(resultingBalance);
        account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyLimits(Account account, BigDecimal amount, TransactionType type) {
        log.info("Verifying limits for transaction type: {}, amount: {}", type, amount);

        if (amount.compareTo(SINGLE_TRANSACTION_LIMIT) > 0) {
            throw new TransferLimitExceededException("Transaction amount exceeds the single transaction limit of " + SINGLE_TRANSACTION_LIMIT);
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        if (type == TransactionType.WITHDRAW) {
            BigDecimal dailyWithdrawalTotal = transactionRepository.calculateDailyWithdrawalTotal(account, startOfDay);
            if (dailyWithdrawalTotal.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
                throw new TransferLimitExceededException("Transaction declined: Exceeds the daily cash withdrawal limit of " + DAILY_WITHDRAWAL_LIMIT);
            }
        } else if (type == TransactionType.TRANSFER || type == TransactionType.BENEFICIARY_TRANSFER || type == TransactionType.INTERNAL_TRANSFER) {
            BigDecimal dailyTransferTotal = transactionRepository.calculateDailyTransferTotal(account, startOfDay);
            if (dailyTransferTotal.add(amount).compareTo(DAILY_TRANSFER_LIMIT) > 0) {
                throw new TransferLimitExceededException("Transaction declined: Exceeds the daily fund transfer limit of " + DAILY_TRANSFER_LIMIT);
            }
        }
    }
}
