package com.novabank.backend.controller;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.User;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.AnalyticsService;
import com.novabank.backend.service.DashboardService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing REST API endpoints for administrative dashboard KPI metrics and analytics charts data.
 * Path mapping: "/api/v1/admin". Protected by stateless JWT authorizations (restricted to ROLE_ADMIN).
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enterprise Admin Dashboard & Analytics Module", description = "APIs to query overarching KPIs and detailed aggregates for system metrics, customer registers, and ledger volumes")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final DashboardService dashboardService;
    private final AnalyticsService analyticsService;

    /**
     * Endpoint to fetch general KPIs and overall stats.
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard overview KPIs", description = "Retrieves high-level summary counts (totals customers, total accounts, today's transaction volume, and total revenue).")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardStats(
            @AuthenticationPrincipal User user
    ) {
        log.info("Admin dashboard statistics requested by admin: {}", user.getEmail());
        DashboardResponse response = dashboardService.getDashboardStats();
        return ResponseUtil.success("Dashboard metrics compiled successfully.", response);
    }

    /**
     * Endpoint to fetch granular Customer Analytics.
     */
    @GetMapping("/analytics/customers")
    @Operation(summary = "Get customer analytics overview", description = "Retrieves active/inactive ratios, verified customers, and pending KYC aggregates.")
    public ResponseEntity<ApiResponse<CustomerAnalyticsResponse>> getCustomerAnalytics(
            @AuthenticationPrincipal User user
    ) {
        log.info("Customer analytics requested by admin: {}", user.getEmail());
        CustomerAnalyticsResponse response = analyticsService.getCustomerAnalytics();
        return ResponseUtil.success("Customer analytics compiled successfully.", response);
    }

    /**
     * Endpoint to fetch granular Account Analytics.
     */
    @GetMapping("/analytics/accounts")
    @Operation(summary = "Get bank accounts analytics", description = "Retrieves total savings, current, frozen, and closed account volumes.")
    public ResponseEntity<ApiResponse<AccountAnalyticsResponse>> getAccountAnalytics(
            @AuthenticationPrincipal User user
    ) {
        log.info("Account analytics requested by admin: {}", user.getEmail());
        AccountAnalyticsResponse response = analyticsService.getAccountAnalytics();
        return ResponseUtil.success("Account analytics compiled successfully.", response);
    }

    /**
     * Endpoint to fetch granular Transaction Analytics.
     */
    @GetMapping("/analytics/transactions")
    @Operation(summary = "Get transaction ledger analytics", description = "Retrieves transaction rates (today, weekly, monthly), success/failed volumes, average limits, and extrema.")
    public ResponseEntity<ApiResponse<TransactionAnalyticsResponse>> getTransactionAnalytics(
            @AuthenticationPrincipal User user
    ) {
        log.info("Transaction analytics requested by admin: {}", user.getEmail());
        TransactionAnalyticsResponse response = analyticsService.getTransactionAnalytics();
        return ResponseUtil.success("Transaction analytics compiled successfully.", response);
    }

    /**
     * Endpoint to fetch granular Loan Analytics.
     */
    @GetMapping("/analytics/loans")
    @Operation(summary = "Get loan analytics stubs", description = "Retrieves stub loan ratios (applied, approved, rejected, outstanding balance). Conforms with core restrictions without loan entity.")
    public ResponseEntity<ApiResponse<LoanAnalyticsResponse>> getLoanAnalytics(
            @AuthenticationPrincipal User user
    ) {
        log.info("Loan analytics requested by admin: {}", user.getEmail());
        LoanAnalyticsResponse response = analyticsService.getLoanAnalytics();
        return ResponseUtil.success("Loan analytics compiled successfully.", response);
    }

    /**
     * Endpoint to fetch granular Card Analytics.
     */
    @GetMapping("/analytics/cards")
    @Operation(summary = "Get card analytics", description = "Retrieves physical vs virtual card issues ratios, blocked status, and expired counts.")
    public ResponseEntity<ApiResponse<CardAnalyticsResponse>> getCardAnalytics(
            @AuthenticationPrincipal User user
    ) {
        log.info("Card analytics requested by admin: {}", user.getEmail());
        CardAnalyticsResponse response = analyticsService.getCardAnalytics();
        return ResponseUtil.success("Card analytics compiled successfully.", response);
    }
}
