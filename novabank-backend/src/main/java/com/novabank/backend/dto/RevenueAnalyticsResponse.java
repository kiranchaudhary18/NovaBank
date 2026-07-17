package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing aggregate fee revenues.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueAnalyticsResponse {

    private BigDecimal loanInterestEarned;  // Stubbed
    private BigDecimal processingFees;      // Stubbed
    private BigDecimal penaltyCharges;      // Stubbed
    private BigDecimal transactionCharges;   // Substituted with sum of transaction fee metrics if applicable
    private BigDecimal totalRevenue;
}
