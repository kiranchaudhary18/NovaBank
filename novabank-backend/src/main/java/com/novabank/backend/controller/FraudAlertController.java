package com.novabank.backend.controller;

import com.novabank.backend.dto.FraudAlertResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AlertStatus;
import com.novabank.backend.enums.AlertType;
import com.novabank.backend.enums.RiskLevel;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.FraudDetectionService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller exposing REST API endpoints for administrative fraud alerts monitoring, reviews, and resolution.
 * Path mapping: "/api/v1/admin/fraud-alerts". Protected by stateless JWT authorizations (restricted to ROLE_ADMIN).
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/admin/fraud-alerts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administrative Fraud Prevention & Alerts Module", description = "APIs to search triggered warnings, audit threats, and perform alerts state resolution reviews")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class FraudAlertController {

    private final FraudDetectionService fraudDetectionService;

    /**
     * Endpoint to list and filter fraud alerts.
     */
    @GetMapping
    @Operation(summary = "Search and list fraud alerts (Paginated)", description = "Searches and filters active/resolved warnings. Supported filters: Risk level, Alert type, status, and customer profile ID.")
    public ResponseEntity<ApiResponse<PagedResponse<FraudAlertResponse>>> searchFraudAlerts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) AlertType alertType,
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) UUID customerId
    ) {
        log.info("Administrative fraud warnings search query requested by: {}", user.getEmail());
        PagedResponse<FraudAlertResponse> response = fraudDetectionService.searchFraudAlerts(
                page, size, sortBy, sortDir, riskLevel, alertType, status, customerId
        );
        return ResponseUtil.success("Fraud alerts list retrieved successfully.", response);
    }

    /**
     * Endpoint to fetch details of a specific fraud alert.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get fraud alert details by ID", description = "Retrieves full details of a specific triggered warning record.")
    public ResponseEntity<ApiResponse<FraudAlertResponse>> getFraudAlertById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Administrative fraud warning details requested for ID: {} by: {}", id, user.getEmail());
        FraudAlertResponse response = fraudDetectionService.getFraudAlertById(id);
        return ResponseUtil.success("Fraud alert details retrieved successfully.", response);
    }

    /**
     * Endpoint to move an alert to UNDER_REVIEW.
     */
    @PatchMapping("/{id}/review")
    @Operation(summary = "Review fraud warning", description = "Marks a warning as UNDER_REVIEW, recording the administrative employee who intercepted the warning.")
    public ResponseEntity<ApiResponse<FraudAlertResponse>> reviewFraudAlert(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Fraud alert review action triggered for ID: {} by: {}", id, user.getEmail());
        FraudAlertResponse response = fraudDetectionService.reviewFraudAlert(id, user.getEmail());
        return ResponseUtil.success("Fraud alert is now under review.", response);
    }

    /**
     * Endpoint to resolve a fraud alert.
     */
    @PatchMapping("/{id}/resolve")
    @Operation(summary = "Resolve fraud warning", description = "Marks a warning as RESOLVED or FALSE_POSITIVE, registering the resolution action.")
    public ResponseEntity<ApiResponse<FraudAlertResponse>> resolveFraudAlert(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam AlertStatus resolutionStatus
    ) {
        log.info("Fraud alert resolution action to {} triggered for ID: {} by: {}", resolutionStatus, id, user.getEmail());
        FraudAlertResponse response = fraudDetectionService.resolveFraudAlert(id, user.getEmail(), resolutionStatus);
        return ResponseUtil.success("Fraud alert resolved successfully.", response);
    }
}
