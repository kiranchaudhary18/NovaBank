package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object carrying bank accounts metrics.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountAnalyticsResponse {

    private long totalAccounts;
    private long savingsAccounts;
    private long currentAccounts;
    private long closedAccounts;
    private long frozenAccounts;
}
