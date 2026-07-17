package com.novabank.backend.service.impl;

import com.novabank.backend.dto.MiniStatementResponse;
import com.novabank.backend.dto.StatementResponse;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.User;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.service.CsvStatementService;
import com.novabank.backend.service.PdfStatementService;
import com.novabank.backend.service.StatementGeneratorService;
import com.novabank.backend.service.TransactionStatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service implementation managing user routing, roles checks, date limits calculations,
 * and statement document generation exports.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionStatementServiceImpl implements TransactionStatementService {

    private final AccountRepository accountRepository;
    private final StatementGeneratorService statementGeneratorService;
    private final PdfStatementService pdfStatementService;
    private final CsvStatementService csvStatementService;

    @Override
    public MiniStatementResponse getMiniStatement(User user, String accountNumber) {
        log.info("Request for Mini Statement on account {} by user {}", accountNumber, user.getEmail());
        Account account = loadAndVerifyAccount(user, accountNumber);
        return statementGeneratorService.generateMiniStatement(account);
    }

    @Override
    public StatementResponse getMonthlyStatement(User user, String accountNumber, int month, int year) {
        log.info("Request for Monthly Statement on account {} for month {}/{} by user {}", accountNumber, month, year, user.getEmail());
        Account account = loadAndVerifyAccount(user, accountNumber);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return statementGeneratorService.generateStatement(account, startDate, endDate);
    }

    @Override
    public StatementResponse getYearlyStatement(User user, String accountNumber, int year) {
        log.info("Request for Yearly Statement on account {} for year {} by user {}", accountNumber, year, user.getEmail());
        Account account = loadAndVerifyAccount(user, accountNumber);

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        return statementGeneratorService.generateStatement(account, startDate, endDate);
    }

    @Override
    public StatementResponse getCustomStatement(User user, String accountNumber, LocalDate startDate, LocalDate endDate) {
        log.info("Request for Custom Statement on account {} from {} to {} by user {}", accountNumber, startDate, endDate, user.getEmail());
        Account account = loadAndVerifyAccount(user, accountNumber);
        return statementGeneratorService.generateStatement(account, startDate, endDate);
    }

    @Override
    public byte[] exportPdfStatement(User user, String accountNumber, LocalDate startDate, LocalDate endDate) {
        log.info("Request for PDF export on account {} from {} to {} by user {}", accountNumber, startDate, endDate, user.getEmail());
        StatementResponse statement = getCustomStatement(user, accountNumber, startDate, endDate);
        return pdfStatementService.generatePdfStatement(statement);
    }

    @Override
    public byte[] exportCsvStatement(User user, String accountNumber, LocalDate startDate, LocalDate endDate) {
        log.info("Request for CSV export on account {} from {} to {} by user {}", accountNumber, startDate, endDate, user.getEmail());
        StatementResponse statement = getCustomStatement(user, accountNumber, startDate, endDate);
        return csvStatementService.generateCsvStatement(statement);
    }

    private Account loadAndVerifyAccount(User user, String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));

        // Security Validation: CUSTOMER can only view statements relating to their own accounts
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            if (!account.getCustomer().getUser().getId().equals(user.getId())) {
                throw new ForbiddenException("Access Denied: You do not own the requested account.");
            }
        }

        return account;
    }
}
