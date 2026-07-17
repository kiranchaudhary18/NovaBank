package com.novabank.backend.service;

import com.novabank.backend.entity.Account;
import com.novabank.backend.enums.TransactionType;

import java.math.BigDecimal;

/**
 * Service interface defining operations related to Account Balance adjustments and validation checks.
 * Enforces limits and minimum balance constraints.
 *
 * @author Senior Java Backend Architect
 */
public interface BalanceService {

    /**
     * Credits a specific account with the deposit amount.
     * Updates both total and available balances.
     *
     * @param account bank account to credit
     * @param amount cash amount to deposit
     */
    void deposit(Account account, BigDecimal amount);

    /**
     * Debits a specific account with the withdrawal amount.
     * Enforces minimum balance requirements.
     * Updates both total and available balances.
     *
     * @param account bank account to debit
     * @param amount cash amount to withdraw
     * @throws com.novabank.backend.exception.InsufficientBalanceException if account has insufficient funds
     */
    void withdraw(Account account, BigDecimal amount);

    /**
     * Verifies single transaction, daily transfer, and withdrawal limits configurations.
     *
     * @param account bank account initiating the transaction
     * @param amount transaction amount to verify
     * @param type transaction category type
     */
    void verifyLimits(Account account, BigDecimal amount, TransactionType type);
}
