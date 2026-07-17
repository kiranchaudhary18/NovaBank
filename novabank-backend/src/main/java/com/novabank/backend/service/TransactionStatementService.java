package com.novabank.backend.service;

import com.novabank.backend.dto.MiniStatementResponse;
import com.novabank.backend.dto.StatementResponse;
import com.novabank.backend.entity.User;

import java.time.LocalDate;

/**
 * Service interface coordinating REST API calls, role security checks, and document exports.
 *
 * @author Senior Java Backend Architect
 */
public interface TransactionStatementService {

    /**
     * Retrieves the last 10 transactions.
     * Accessible by account owners, employees, and administrators.
     *
     * @param user authenticated caller
     * @param accountNumber bank account number
     * @return MiniStatementResponse payload
     */
    MiniStatementResponse getMiniStatement(User user, String accountNumber);

    /**
     * Retrieves transactions for a specific calendar month.
     *
     * @param user authenticated caller
     * @param accountNumber bank account number
     * @param month target month (1 to 12)
     * @param year target year
     * @return StatementResponse containing summaries and transactions
     */
    StatementResponse getMonthlyStatement(User user, String accountNumber, int month, int year);

    /**
     * Retrieves transactions for a specific calendar year.
     *
     * @param user authenticated caller
     * @param accountNumber bank account number
     * @param year target year
     * @return StatementResponse containing summaries and transactions
     */
    StatementResponse getYearlyStatement(User user, String accountNumber, int year);

    /**
     * Retrieves transactions for a custom range.
     *
     * @param user authenticated caller
     * @param accountNumber bank account number
     * @param startDate start date
     * @param endDate end date
     * @return StatementResponse containing summaries and transactions
     */
    StatementResponse getCustomStatement(User user, String accountNumber, LocalDate startDate, LocalDate endDate);

    /**
     * Compiles custom statements into a downloadable PDF binary byte stream.
     *
     * @param user authenticated caller
     * @param accountNumber bank account number
     * @param startDate start date
     * @param endDate end date
     * @return raw binary PDF byte array
     */
    byte[] exportPdfStatement(User user, String accountNumber, LocalDate startDate, LocalDate endDate);

    /**
     * Compiles custom statements into a downloadable CSV binary byte stream.
     *
     * @param user authenticated caller
     * @param accountNumber bank account number
     * @param startDate start date
     * @param endDate end date
     * @return raw binary CSV byte array
     */
    byte[] exportCsvStatement(User user, String accountNumber, LocalDate startDate, LocalDate endDate);
}
