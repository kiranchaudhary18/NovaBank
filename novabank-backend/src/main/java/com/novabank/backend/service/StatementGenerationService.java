package com.novabank.backend.service;

/**
 * Service interface compiling monthly accounts statements and archiving records.
 *
 * @author Senior Java Backend Architect
 */
public interface StatementGenerationService {

    /**
     * Aggregates and triggers monthly statement generation batches for active accounts.
     *
     * @return count of statements compiled
     */
    int generateMonthlyStatements();
}
