package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object carrying granular customer registration analytics.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAnalyticsResponse {

    private long totalCustomers;
    private long activeCustomers;
    private long inactiveCustomers;
    private long blockedCustomers;
    private long verifiedCustomers; // Approved KYC
    private long pendingKyc;
}
