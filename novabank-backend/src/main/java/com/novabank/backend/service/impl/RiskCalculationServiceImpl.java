package com.novabank.backend.service.impl;

import com.novabank.backend.dto.RiskScoreResponse;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.FraudAlert;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AlertStatus;
import com.novabank.backend.enums.RiskLevel;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.repository.FraudAlertRepository;
import com.novabank.backend.service.RiskCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation managing risk scoring matrices.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskCalculationServiceImpl implements RiskCalculationService {

    private final CustomerRepository customerRepository;
    private final FraudAlertRepository fraudAlertRepository;

    @Override
    @Transactional(readOnly = true)
    public RiskScoreResponse calculateCustomerRiskScore(UUID customerId) {
        log.info("Calculating security risk profile for customer ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        User user = customer.getUser();
        List<FraudAlert> alerts = fraudAlertRepository.findByUser(user);

        // Filter for active alerts (OPEN or UNDER_REVIEW status)
        List<FraudAlert> activeAlerts = alerts.stream()
                .filter(a -> a.getStatus() == AlertStatus.OPEN || a.getStatus() == AlertStatus.UNDER_REVIEW)
                .toList();

        int totalScore = activeAlerts.stream()
                .mapToInt(FraudAlert::getRiskScore)
                .sum();

        RiskLevel riskLevel;
        if (totalScore >= 70) {
            riskLevel = RiskLevel.CRITICAL;
        } else if (totalScore >= 45) {
            riskLevel = RiskLevel.HIGH;
        } else if (totalScore >= 20) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.LOW;
        }

        List<String> rulesSummary = activeAlerts.stream()
                .map(a -> a.getAlertType().name() + " (Score: " + a.getRiskScore() + ") - " + a.getReason())
                .toList();

        String fullName = customer.getFirstName() + " " + customer.getLastName();

        return RiskScoreResponse.builder()
                .customerId(customer.getId())
                .customerName(fullName)
                .totalRiskScore(totalScore)
                .riskLevel(riskLevel)
                .activeAlertsCount(activeAlerts.size())
                .triggeredRulesSummary(rulesSummary)
                .build();
    }
}
