package com.novabank.backend.service;

import com.novabank.backend.dto.MiniStatementResponse;
import com.novabank.backend.dto.StatementResponse;
import com.novabank.backend.entity.Account;

import java.time.LocalDate;

/**
 * Service interface defining operations to calculate account balance summaries
 * and format ledger lists.
 *
 * @author Senior Java Backend Architect
 */
public interface StatementGeneratorService {

    /**
     * Queries database ledger transactions and generates a custom statement response
     * for a given date range.
     *
     * @param account target bank account
     * @param startDate period start date
     * @param endDate period end date
     * @return StatementResponse containing summaries and ledger list
     */
    StatementResponse generateStatement(Account account, LocalDate startDate, LocalDate endDate);

    /**
     * Generates a mini statement (last 10 transactions) for a given account.
     *
     * @param account target bank account
     * @return MiniStatementResponse DTO
     */
    MiniStatementResponse generateMiniStatement(Account account);
}
