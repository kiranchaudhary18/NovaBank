package com.novabank.backend.controller;

import com.novabank.backend.dto.MiniStatementResponse;
import com.novabank.backend.dto.StatementResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.TransactionStatementService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller exposing REST API endpoints for bank statements and passbooks.
 * Path mapping: "/api/v1/statements". Protected by stateless JWT authorizations.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/statements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bank Statement & Exports Module", description = "APIs to fetch mini statements, monthly statements, yearly statements, and custom exports in PDF and CSV format")
@SecurityRequirement(name = "bearerAuth")
public class StatementController {

    private final TransactionStatementService transactionStatementService;

    /**
     * Endpoint to fetch custom range statements.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get custom statement details", description = "Retrieves statement details including summaries and chronological transaction history for a custom range. Customers can only view statements relating to their own accounts.")
    public ResponseEntity<ApiResponse<StatementResponse>> getCustomStatement(
            @AuthenticationPrincipal User user,
            @RequestParam String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("Request for custom statement from {} to {} for account: {}", startDate, endDate, accountNumber);
        StatementResponse response = transactionStatementService.getCustomStatement(user, accountNumber, startDate, endDate);
        return ResponseUtil.success("Bank statement retrieved successfully.", response);
    }

    /**
     * Endpoint to retrieve mini statement.
     */
    @GetMapping("/mini")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get mini statement", description = "Retrieves details of the last 10 transactions. Customers can only view statements relating to their own accounts.")
    public ResponseEntity<ApiResponse<MiniStatementResponse>> getMiniStatement(
            @AuthenticationPrincipal User user,
            @RequestParam String accountNumber
    ) {
        log.info("Request for mini statement for account: {}", accountNumber);
        MiniStatementResponse response = transactionStatementService.getMiniStatement(user, accountNumber);
        return ResponseUtil.success("Mini statement retrieved successfully.", response);
    }

    /**
     * Endpoint to retrieve monthly statement.
     */
    @GetMapping("/monthly")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get monthly statement", description = "Retrieves statements for a specific calendar month. Customers can only view statements relating to their own accounts.")
    public ResponseEntity<ApiResponse<StatementResponse>> getMonthlyStatement(
            @AuthenticationPrincipal User user,
            @RequestParam String accountNumber,
            @RequestParam int month,
            @RequestParam int year
    ) {
        log.info("Request for monthly statement month: {} year: {} for account: {}", month, year, accountNumber);
        StatementResponse response = transactionStatementService.getMonthlyStatement(user, accountNumber, month, year);
        return ResponseUtil.success("Monthly statement retrieved successfully.", response);
    }

    /**
     * Endpoint to retrieve yearly statement.
     */
    @GetMapping("/yearly")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get yearly statement", description = "Retrieves statements for a specific calendar year. Customers can only view statements relating to their own accounts.")
    public ResponseEntity<ApiResponse<StatementResponse>> getYearlyStatement(
            @AuthenticationPrincipal User user,
            @RequestParam String accountNumber,
            @RequestParam int year
    ) {
        log.info("Request for yearly statement year: {} for account: {}", year, accountNumber);
        StatementResponse response = transactionStatementService.getYearlyStatement(user, accountNumber, year);
        return ResponseUtil.success("Yearly statement retrieved successfully.", response);
    }

    /**
     * Endpoint to export statement as PDF.
     */
    @GetMapping("/pdf")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Download PDF Statement", description = "Generates and streams a professional PDF statement document containing account details and transaction history. Customers can only download statements relating to their own accounts.")
    public ResponseEntity<byte[]> downloadPdfStatement(
            @AuthenticationPrincipal User user,
            @RequestParam String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("PDF download request from {} to {} for account: {}", startDate, endDate, accountNumber);
        byte[] pdfContent = transactionStatementService.exportPdfStatement(user, accountNumber, startDate, endDate);
        String filename = "statement_" + accountNumber + "_" + startDate + "_to_" + endDate + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    /**
     * Endpoint to export statement as CSV.
     */
    @GetMapping("/csv")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Download CSV Statement", description = "Generates and streams a downloadable CSV file. Customers can only download statements relating to their own accounts.")
    public ResponseEntity<byte[]> downloadCsvStatement(
            @AuthenticationPrincipal User user,
            @RequestParam String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("CSV download request from {} to {} for account: {}", startDate, endDate, accountNumber);
        byte[] csvContent = transactionStatementService.exportCsvStatement(user, accountNumber, startDate, endDate);
        String filename = "statement_" + accountNumber + "_" + startDate + "_to_" + endDate + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvContent);
    }
}
