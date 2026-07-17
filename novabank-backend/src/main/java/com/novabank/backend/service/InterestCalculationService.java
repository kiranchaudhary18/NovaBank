package com.novabank.backend.service;

/**
 * Service interface executing savings accounts interest yield ledger credits.
 *
 * @author Senior Java Backend Architect
 */
public interface InterestCalculationService {

    /**
     * Scans and applies daily interest rates on active Savings Accounts.
     * Processes records in paginated batches and commits transactions.
     *
     * @return count of processed accounts successfully credited
     */
    int calculateDailyInterest();
}
