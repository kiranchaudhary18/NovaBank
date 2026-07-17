package com.novabank.backend.service;

import com.novabank.backend.dto.*;

/**
 * Service interface executing detailed aggregation audits across different business domain modules.
 *
 * @author Senior Java Backend Architect
 */
public interface AnalyticsService {

    /**
     * Aggregates stats about customer registrations and verification flows.
     *
     * @return CustomerAnalyticsResponse dataset
     */
    CustomerAnalyticsResponse getCustomerAnalytics();

    /**
     * Aggregates stats about deposit accounts, categorizations, and states.
     *
     * @return AccountAnalyticsResponse dataset
     */
    AccountAnalyticsResponse getAccountAnalytics();

    /**
     * Compiles transaction logs totals, volumes, averages, and extremes.
     *
     * @return TransactionAnalyticsResponse dataset
     */
    TransactionAnalyticsResponse getTransactionAnalytics();

    /**
     * Compiles placeholder loan metrics.
     *
     * @return LoanAnalyticsResponse dataset
     */
    LoanAnalyticsResponse getLoanAnalytics();

    /**
     * Aggregates counts for active, blocked, and expired debit and virtual cards.
     *
     * @return CardAnalyticsResponse dataset
     */
    CardAnalyticsResponse getCardAnalytics();

    /**
     * Compiles platform fee earnings and penalties interest revenues.
     *
     * @return RevenueAnalyticsResponse dataset
     */
    RevenueAnalyticsResponse getRevenueAnalytics();
}
