package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object containing high-level summary metrics for the Admin Dashboard.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private long totalCustomers;
    private long activeCustomers;
    private long pendingKycCount;
    private long totalAccounts;
    private BigDecimal totalBalance;
    private long totalTransactionsToday;
    private BigDecimal totalTransactionVolumeToday;
    private BigDecimal totalOutstandingLoans; // Stubbed loan aggregation metric
    private BigDecimal totalRevenueToday;     // Fees and penalty charges
}
