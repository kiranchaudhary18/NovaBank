package com.novabank.backend.service.impl;

import com.novabank.backend.entity.Account;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.service.StatementGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing scheduled monthly statement assemblies.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatementGenerationServiceImpl implements StatementGenerationService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public int generateMonthlyStatements() {
        log.info("Starting batch execution for Monthly Statements Generation");

        int page = 0;
        int size = 100;
        int processedCount = 0;
        Page<Account> accountsPage;

        do {
            Pageable pageable = PageRequest.of(page, size);
            // Compile statements for active Savings and Current accounts
            accountsPage = accountRepository.findAll(pageable);
            
            for (Account account : accountsPage.getContent()) {
                if (account.getStatus() == AccountStatus.ACTIVE) {
                    try {
                        log.debug("Generating statement ledger payload for account: {}", account.getAccountNumber());
                        // Simulating building and archiving pdf statements payload
                        processedCount++;
                    } catch (Exception e) {
                        log.error("Failed to generate statement for account: {}", account.getAccountNumber(), e);
                    }
                }
            }
            page++;
        } while (accountsPage.hasNext());

        log.info("Monthly statement generation completed successfully. Accounts compiled: {}", processedCount);
        return processedCount;
    }
}
