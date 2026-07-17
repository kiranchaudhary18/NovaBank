package com.novabank.backend.controller;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.AccountService;
import com.novabank.backend.service.CustomerService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing REST API endpoints for Bank Account Management.
 * Path mapping: "/api/v1/accounts". Protected by stateless JWT authorizations.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bank Account Management Module", description = "APIs to manage customers' savings, current, and deposit accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;
    private final CustomerService customerService;

    /**
     * Endpoint to open a new customer account.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Open a bank account", description = "Creates a new bank account (SAVINGS, CURRENT, or FIXED_DEPOSIT) for a customer profile. Customers can only open accounts for themselves. Employees/Admins can open for anyone.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Bank account opened successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "KYC not verified, duplicate primary savings account exists, or validation failure.")
    })
    public ResponseEntity<ApiResponse<AccountResponse>> openAccount(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateAccountRequest request
    ) {
        log.info("Request to open account by user: {} for customer ID: {}", user.getEmail(), request.getCustomerId());

        // Security check: Customer can only open accounts for themselves
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            if (!customerProfile.getId().equals(request.getCustomerId())) {
                throw new ForbiddenException("Unauthorized: You can only open accounts for your own customer profile.");
            }
        }

        AccountResponse response = accountService.createAccount(request);
        return ResponseUtil.created("Bank account opened successfully.", response);
    }

    /**
     * Endpoint to retrieve account details by UUID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get account details by ID", description = "Retrieves details of a bank account by ID. Owners can view their own accounts; Employees/Admins can view any account.")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Request to fetch account ID: {} by user: {}", id, user.getEmail());
        AccountResponse response = accountService.getAccountById(id);

        // Security check: Customer can only view their own account details
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            if (!response.getCustomerId().equals(customerProfile.getId())) {
                throw new ForbiddenException("Unauthorized: You do not own this account.");
            }
        }

        return ResponseUtil.success("Account details retrieved successfully.", response);
    }

    /**
     * Endpoint to retrieve account details by Account Number.
     */
    @GetMapping("/account-number/{number}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get account details by Account Number", description = "Retrieves bank account details by account number. Customers can only look up their own accounts.")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(
            @AuthenticationPrincipal User user,
            @PathVariable String number
    ) {
        log.info("Request to fetch account number: {} by user: {}", number, user.getEmail());
        AccountResponse response = accountService.getAccountByAccountNumber(number);

        // Security check: Customer can only view their own account details
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            if (!response.getCustomerId().equals(customerProfile.getId())) {
                throw new ForbiddenException("Unauthorized: You do not own this account.");
            }
        }

        return ResponseUtil.success("Account details retrieved successfully.", response);
    }

    /**
     * Endpoint to search and filter accounts (Employee and Admin restricted).
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Search and filter accounts (Paginated)", description = "Performs dynamic searches and filters on bank accounts. Restricted to EMPLOYEE and ADMIN roles only.")
    public ResponseEntity<ApiResponse<PagedResponse<AccountResponse>>> searchAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) AccountType type,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) LocalDate openedDate,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) UUID customerId
    ) {
        log.info("Search request for accounts - Type: {}, Status: {}", type, status);
        PagedResponse<AccountResponse> response = accountService.searchAccounts(
                page, size, sortBy, sortDir, type, status, openedDate, accountNumber, customerId
        );
        return ResponseUtil.success("Accounts search completed successfully.", response);
    }

    /**
     * Endpoint to update account details.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Update account properties", description = "Updates details (status or primary settings) of an account. Restricted to EMPLOYEE and ADMIN roles only.")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequest request
    ) {
        log.info("Update request for account ID: {}", id);
        AccountResponse response = accountService.updateAccount(id, request);
        return ResponseUtil.success("Account details updated successfully.", response);
    }

    /**
     * Endpoint to freeze an account.
     */
    @PatchMapping("/{id}/freeze")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Freeze account status", description = "Freezes a bank account, blocking transactions. Restricted to EMPLOYEE and ADMIN roles only.")
    public ResponseEntity<ApiResponse<AccountResponse>> freezeAccount(@PathVariable UUID id) {
        log.info("Freeze request for account ID: {}", id);
        AccountResponse response = accountService.freezeAccount(id);
        return ResponseUtil.success("Account has been frozen successfully.", response);
    }

    /**
     * Endpoint to reactivate a frozen/blocked account.
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Reactivate bank account status", description = "Reactivates a frozen or blocked bank account. Restricted to EMPLOYEE and ADMIN roles only.")
    public ResponseEntity<ApiResponse<AccountResponse>> activateAccount(@PathVariable UUID id) {
        log.info("Activation request for account ID: {}", id);
        AccountResponse response = accountService.activateAccount(id);
        return ResponseUtil.success("Account has been reactivated successfully.", response);
    }

    /**
     * Endpoint to close an account.
     */
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Close bank account status", description = "Closes a bank account. Once closed, an account cannot be modified or reactivated. Restricted to EMPLOYEE and ADMIN roles only.")
    public ResponseEntity<ApiResponse<AccountResponse>> closeAccount(@PathVariable UUID id) {
        log.info("Close request for account ID: {}", id);
        AccountResponse response = accountService.closeAccount(id);
        return ResponseUtil.success("Account has been closed successfully.", response);
    }

    /**
     * Endpoint to toggle an account as the primary savings account.
     */
    @PatchMapping("/{id}/primary")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Designate primary savings account", description = "Designates this account as the primary savings account. Disables primary settings on all other accounts owned by the customer.")
    public ResponseEntity<ApiResponse<AccountResponse>> setPrimaryAccount(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Set-primary request for account ID: {} by customer: {}", id, user.getEmail());
        AccountResponse account = accountService.getAccountById(id);

        // Security check: Only the account owner customer can configure it as primary
        CustomerResponse customerProfile = customerService.getMyProfile(user);
        if (!account.getCustomerId().equals(customerProfile.getId())) {
            throw new ForbiddenException("Unauthorized: You do not own this account.");
        }

        AccountResponse response = accountService.setPrimaryAccount(id);
        return ResponseUtil.success("Account set as primary savings successfully.", response);
    }

    /**
     * Endpoint to get list of account summaries for the authenticated customer.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Get list of own accounts summaries", description = "Retrieves lightweight details summaries of all bank accounts owned by the authenticated customer.")
    public ResponseEntity<ApiResponse<List<AccountSummaryResponse>>> getMyAccountsSummary(@AuthenticationPrincipal User user) {
        log.info("Request for accounts summaries for user: {}", user.getEmail());
        CustomerResponse customerProfile = customerService.getMyProfile(user);
        List<AccountSummaryResponse> summaries = accountService.getAccountsByCustomerId(customerProfile.getId());
        return ResponseUtil.success("Account summaries retrieved successfully.", summaries);
    }
}
