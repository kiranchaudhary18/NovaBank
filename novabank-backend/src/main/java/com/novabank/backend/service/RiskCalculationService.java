package com.novabank.backend.service;

import com.novabank.backend.dto.RiskScoreResponse;

import java.util.UUID;

/**
 * Service interface executing dynamic risk scoring and threat level classification.
 *
 * @author Senior Java Backend Architect
 */
public interface RiskCalculationService {

    /**
     * Aggregates total risk indicators scoring and rules triggered for a customer.
     *
     * @param customerId target customer UUID
     * @return RiskScoreResponse details
     */
    RiskScoreResponse calculateCustomerRiskScore(UUID customerId);
}
