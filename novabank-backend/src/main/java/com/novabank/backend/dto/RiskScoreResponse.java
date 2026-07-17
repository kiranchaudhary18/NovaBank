package com.novabank.backend.dto;

import com.novabank.backend.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing customer account aggregated threat risk profile.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskScoreResponse {

    private UUID customerId;
    private String customerName;
    private int totalRiskScore;
    private RiskLevel riskLevel;
    private long activeAlertsCount;
    private List<String> triggeredRulesSummary;
}
