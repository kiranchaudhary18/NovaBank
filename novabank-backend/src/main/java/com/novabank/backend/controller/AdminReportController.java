package com.novabank.backend.controller;

import com.novabank.backend.entity.User;
import com.novabank.backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing REST API endpoints to export administrative CSV and PDF reports.
 * Path mapping: "/api/v1/admin/reports". Protected by stateless JWT authorizations (restricted to ROLE_ADMIN).
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administrative Reports & Exports Module", description = "APIs to download system reports in PDF or CSV format")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    /**
     * Endpoint to download customer profiles report.
     */
    @GetMapping("/customers")
    @Operation(summary = "Export customers report", description = "Downloads customer profiles list. Supported formats: CSV, PDF.")
    public ResponseEntity<byte[]> getCustomerReport(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateRange
    ) {
        log.info("Customer report download requested by admin: {}", user.getEmail());
        byte[] data = reportService.generateCustomerReport(format, status, dateRange);
        return buildFileResponse(data, "customer_report", format);
    }

    /**
     * Endpoint to download accounts report.
     */
    @GetMapping("/accounts")
    @Operation(summary = "Export accounts report", description = "Downloads savings/current accounts list. Supported formats: CSV, PDF.")
    public ResponseEntity<byte[]> getAccountReport(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateRange
    ) {
        log.info("Account report download requested by admin: {}", user.getEmail());
        byte[] data = reportService.generateAccountReport(format, status, dateRange);
        return buildFileResponse(data, "account_report", format);
    }

    /**
     * Endpoint to download transactions report.
     */
    @GetMapping("/transactions")
    @Operation(summary = "Export transactions report", description = "Downloads transactions ledger. Supported formats: CSV, PDF.")
    public ResponseEntity<byte[]> getTransactionReport(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dateRange
    ) {
        log.info("Transaction report download requested by admin: {}", user.getEmail());
        byte[] data = reportService.generateTransactionReport(format, type, dateRange);
        return buildFileResponse(data, "transaction_report", format);
    }

    /**
     * Endpoint to download loan report stubs.
     */
    @GetMapping("/loans")
    @Operation(summary = "Export loan stubs report", description = "Downloads mock loan applications list. Supported formats: CSV, PDF.")
    public ResponseEntity<byte[]> getLoanReport(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateRange
    ) {
        log.info("Loan report download requested by admin: {}", user.getEmail());
        byte[] data = reportService.generateLoanReport(format, status, dateRange);
        return buildFileResponse(data, "loan_report", format);
    }

    /**
     * Endpoint to download revenue report.
     */
    @GetMapping("/revenue")
    @Operation(summary = "Export revenue streams report", description = "Downloads summary of fee collection and penance charges. Supported formats: CSV, PDF.")
    public ResponseEntity<byte[]> getRevenueReport(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String dateRange
    ) {
        log.info("Revenue report download requested by admin: {}", user.getEmail());
        byte[] data = reportService.generateRevenueReport(format, dateRange);
        return buildFileResponse(data, "revenue_report", format);
    }

    // ==========================================
    // PRIVATE RESPONSE BUILDER UTILITY
    // ==========================================

    private ResponseEntity<byte[]> buildFileResponse(byte[] data, String baseName, String format) {
        String contentType = format.equalsIgnoreCase("CSV") ? "text/csv" : "application/pdf";
        String extension = format.equalsIgnoreCase("CSV") ? ".csv" : ".pdf";
        String filename = baseName + "_" + System.currentTimeMillis() + extension;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(data);
    }
}
