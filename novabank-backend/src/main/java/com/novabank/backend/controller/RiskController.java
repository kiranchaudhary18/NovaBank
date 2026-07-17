package com.novabank.backend.controller;

import com.novabank.backend.dto.RiskScoreResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.RiskCalculationService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller exposing REST API endpoints to fetch customer risk scores and warning profiles.
 * Path mapping: "/api/v1/admin/security". Protected by stateless JWT authorizations (restricted to ROLE_ADMIN).
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/admin/security")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administrative Security & Threat Scoring Module", description = "APIs to query aggregates customer risk scores and rules violation metrics")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RiskController {

    private final RiskCalculationService riskCalculationService;

    /**
     * Endpoint to fetch a customer's total risk score dataset.
     */
    @GetMapping("/risk-score/{customerId}")
    @Operation(summary = "Get customer risk score details", description = "Aggregates open warnings for a customer and returns the current total threat score and rules violation summary.")
    public ResponseEntity<ApiResponse<RiskScoreResponse>> getCustomerRiskScore(
            @AuthenticationPrincipal User user,
            @PathVariable UUID customerId
    ) {
        log.info("Administrative risk score query requested for customer ID: {} by: {}", customerId, user.getEmail());
        RiskScoreResponse response = riskCalculationService.calculateCustomerRiskScore(customerId);
        return ResponseUtil.success("Customer risk profile calculated successfully.", response);
    }
}
