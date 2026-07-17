package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object containing transactions analytics (periods aggregates, volumes, averages, and extremes).
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionAnalyticsResponse {

    private long todayTransactions;
    private long yesterdayTransactions;
    private long weeklyTransactions;
    private long monthlyTransactions;
    private long yearlyTransactions;
    private long successfulTransactions;
    private long failedTransactions;
    private long pendingTransactions;
    private BigDecimal averageTransactionAmount;
    private BigDecimal highestTransaction;
    private BigDecimal lowestTransaction;
}
