package com.novabank.backend.service.impl;

import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.service.AccountNumberGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for generating unique bank account numbers and branch IFSC codes.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountNumberGeneratorServiceImpl implements AccountNumberGeneratorService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public synchronized String generateAccountNumber() {
        long count = accountRepository.count();
        String accountNumber;
        long sequence = count + 1;

        // Loop to ensure uniqueness in case of gaps in sequential count
        do {
            accountNumber = String.format("NB1%08d", sequence++);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        log.debug("Generated unique account number: {}", accountNumber);
        return accountNumber;
    }

    @Override
    public String generateIfscCode(String branchCode) {
        if (branchCode == null || branchCode.isBlank()) {
            return "NOVA0001001"; // Default head branch code
        }

        // Sanitize and extract only digits from the branchCode
        String numericPart = branchCode.replaceAll("[^0-9]", "");
        if (numericPart.isBlank()) {
            numericPart = "1001";
        }

        // Pad to exactly 6 digits to maintain the 11-digit IFSC standard: NOVA0[6-digits]
        if (numericPart.length() > 6) {
            numericPart = numericPart.substring(0, 6);
        }

        int branchInt = Integer.parseInt(numericPart);
        if (branchInt > 999999) {
            branchInt = branchInt % 1000000;
        }

        String ifsc = String.format("NOVA0%06d", branchInt);
        log.debug("Generated IFSC code: {} for branch: {}", ifsc, branchCode);
        return ifsc;
    }
}
