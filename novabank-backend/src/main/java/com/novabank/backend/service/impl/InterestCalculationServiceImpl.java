package com.novabank.backend.service.impl;

import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;
import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.TransactionRepository;
import com.novabank.backend.service.InterestCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service implementation calculating and depositing daily interest yields.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterestCalculationServiceImpl implements InterestCalculationService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private static final BigDecimal ANNUAL_RATE = new BigDecimal("0.04"); // 4% Savings yield rate
    private static final BigDecimal DAYS_IN_YEAR = new BigDecimal("365");

    @Override
    @Transactional
    public int calculateDailyInterest() {
        log.info("Starting Daily Interest Calculation Batch");

        int page = 0;
        int size = 100;
        int interestAppliedCount = 0;
        Page<Account> savingsAccounts;

        do {
            Pageable pageable = PageRequest.of(page, size);
            savingsAccounts = accountRepository.findByAccountTypeAndStatus(
                    AccountType.SAVINGS, AccountStatus.ACTIVE, pageable
            );

            for (Account account : savingsAccounts.getContent()) {
                try {
                    BigDecimal balance = account.getBalance();
                    if (balance.compareTo(BigDecimal.ZERO) <= 0) {
                        continue; // Skip negative or zero balance accounts
                    }

                    // Interest = Balance * (Annual Rate / 365)
                    BigDecimal dailyRate = ANNUAL_RATE.divide(DAYS_IN_YEAR, 8, RoundingMode.HALF_UP);
                    BigDecimal interestEarned = balance.multiply(dailyRate).setScale(4, RoundingMode.HALF_UP);

                    if (interestEarned.compareTo(BigDecimal.ZERO) > 0) {
                        // Apply interest to balance
                        account.setBalance(balance.add(interestEarned));
                        accountRepository.save(account);

                        // Record financial deposit transaction
                        Transaction txn = Transaction.builder()
                                .transactionId("INT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase())
                                .referenceNumber("REF_INT_" + System.currentTimeMillis())
                                .receiverAccount(account)
                                .transactionType(TransactionType.DEPOSIT)
                                .amount(interestEarned)
                                .status(TransactionStatus.SUCCESS)
                                .remarks("Daily Interest Yield Deposit")
                                .transactionDate(LocalDateTime.now())
                                .build();

                        transactionRepository.save(txn);
                        interestAppliedCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to process daily interest for account: {}", account.getAccountNumber(), e);
                }
            }
            page++;
        } while (savingsAccounts.hasNext());

        log.info("Daily Interest Calculation Batch completed. Accounts processed: {}", interestAppliedCount);
        return interestAppliedCount;
    }
}
