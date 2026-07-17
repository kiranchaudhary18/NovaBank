package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing loan metrics stubs.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAnalyticsResponse {

    private long applied;
    private long approved;
    private long rejected;
    private long active;
    private long closed;
    private long overdue;
    private BigDecimal totalLoanAmount;
    private BigDecimal outstandingBalance;
}
