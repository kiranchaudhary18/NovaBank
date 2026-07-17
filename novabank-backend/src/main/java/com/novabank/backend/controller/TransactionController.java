package com.novabank.backend.controller;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.AccountService;
import com.novabank.backend.service.CustomerService;
import com.novabank.backend.service.TransactionService;
import com.novabank.backend.service.TransferService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Controller exposing core banking transaction REST endpoints.
 * Path mapping: "/api/v1/transactions". Protected by stateless JWT authorizations.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Core Banking Transaction Engine Module", description = "APIs for cash deposits, cash withdrawals, fund transfers, and ledger lookups")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransferService transferService;
    private final AccountService accountService;
    private final CustomerService customerService;

    /**
     * Endpoint to execute cash deposits.
     */
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Execute a cash deposit", description = "Credits cash funds into a bank account. Customers can only deposit into their own accounts. Employees/Admins can credit any account.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deposit executed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Account not active, maximum single deposit limit exceeded, or validation error.")
    })
    public ResponseEntity<ApiResponse<TransactionReceiptResponse>> deposit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DepositRequest request
    ) {
        log.info("Request to execute deposit of {} by user: {}", request.getAmount(), user.getEmail());

        // Security check: Customer can only credit their own account
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            AccountResponse account = accountService.getAccountByAccountNumber(request.getAccountNumber());
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            if (!account.getCustomerId().equals(customerProfile.getId())) {
                throw new ForbiddenException("Access Denied: You do not own the target account.");
            }
        }

        TransactionReceiptResponse response = transactionService.deposit(user, request);
        return ResponseUtil.success("Cash deposit processed successfully.", response);
    }

    /**
     * Endpoint to execute cash withdrawals.
     */
    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Execute a cash withdrawal", description = "Debits cash funds from a bank account. Customers can only withdraw from their own accounts. Enforces daily withdrawal limits and minimum balance.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Withdrawal executed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Insufficient funds, limit exceeded, minimum balance violation, or validation error.")
    })
    public ResponseEntity<ApiResponse<TransactionReceiptResponse>> withdraw(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WithdrawRequest request
    ) {
        log.info("Request to execute withdrawal of {} by user: {}", request.getAmount(), user.getEmail());

        // Security check: Customer can only withdraw from their own account
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            AccountResponse account = accountService.getAccountByAccountNumber(request.getAccountNumber());
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            if (!account.getCustomerId().equals(customerProfile.getId())) {
                throw new ForbiddenException("Access Denied: You do not own this account.");
            }
        }

        TransactionReceiptResponse response = transactionService.withdraw(user, request);
        return ResponseUtil.success("Cash withdrawal processed successfully.", response);
    }

    /**
     * Endpoint to execute fund transfers.
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Transfer funds", description = "Initiates a fund transfer from a sender account to a receiver account. Customers can only transfer from their own accounts. Enforces daily transfer limits, active statuses, and saved beneficiary blocks.")
    public ResponseEntity<ApiResponse<TransactionReceiptResponse>> transfer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransferRequest request
    ) {
        log.info("Request to transfer {} from {} to {}", request.getAmount(), request.getSenderAccountNumber(), request.getReceiverAccountNumber());
        TransactionReceiptResponse response = transferService.transferFunds(user, request);
        return ResponseUtil.success("Fund transfer completed successfully.", response);
    }

    /**
     * Endpoint to retrieve details of a specific transaction by its UUID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get transaction details by ID", description = "Retrieves ledger details of a transaction by ID. Customers can only view transactions related to their own accounts.")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Request to fetch transaction ID: {} by user: {}", id, user.getEmail());
        TransactionResponse response = transactionService.getTransactionById(id);

        // Security check: Customer can only view transactions relating to their own accounts
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            verifyTransactionOwnership(response, customerProfile.getId());
        }

        return ResponseUtil.success("Transaction details retrieved successfully.", response);
    }

    /**
     * Endpoint to retrieve details of a specific transaction by its reference number.
     */
    @GetMapping("/reference/{referenceNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get transaction details by Reference Number", description = "Retrieves details of a transaction by reference number. Customers can only view transactions related to their own accounts.")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionByReference(
            @AuthenticationPrincipal User user,
            @PathVariable String referenceNumber
    ) {
        log.info("Request to fetch transaction reference: {} by user: {}", referenceNumber, user.getEmail());
        TransactionResponse response = transactionService.getTransactionByReference(referenceNumber);

        // Security check: Customer can only view transactions relating to their own accounts
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            verifyTransactionOwnership(response, customerProfile.getId());
        }

        return ResponseUtil.success("Transaction details retrieved successfully.", response);
    }

    /**
     * Endpoint to list paginated transactions associated with a specific account number.
     */
    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get transactions by Account Number (Paginated)", description = "Retrieves all transactions associated with an account (either as sender or receiver). Customers can only view their own accounts.")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getTransactionsByAccount(
            @AuthenticationPrincipal User user,
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        log.info("Request to fetch transactions list for account: {} by user: {}", accountNumber, user.getEmail());

        // Security check: Customer can only view transactions relating to their own accounts
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            AccountResponse account = accountService.getAccountByAccountNumber(accountNumber);
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            if (!account.getCustomerId().equals(customerProfile.getId())) {
                throw new ForbiddenException("Access Denied: You do not own this account.");
            }
        }

        PagedResponse<TransactionResponse> response = transactionService.getTransactionsByAccount(accountNumber, page, size, sortBy, sortDir);
        return ResponseUtil.success("Transactions list retrieved successfully.", response);
    }

    /**
     * Endpoint to search and filter transactions dynamically (Employee and Admin restricted).
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Search and filter transactions ledger (Paginated)", description = "Performs dynamic searches and filters on transactions. Restricted to EMPLOYEE and ADMIN roles only.")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> searchTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String senderAccountNumber,
            @RequestParam(required = false) String receiverAccountNumber,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end
    ) {
        log.info("Search request for transactions - Ref: {}, ID: {}", referenceNumber, transactionId);
        PagedResponse<TransactionResponse> response = transactionService.searchTransactions(
                page, size, sortBy, sortDir, referenceNumber, transactionId,
                senderAccountNumber, receiverAccountNumber, minAmount, maxAmount, status, type, start, end
        );
        return ResponseUtil.success("Transactions search completed successfully.", response);
    }

    private void verifyTransactionOwnership(TransactionResponse transaction, UUID customerProfileId) {
        boolean ownsSender = false;
        boolean ownsReceiver = false;

        if (transaction.getSenderAccountNumber() != null) {
            AccountResponse sender = accountService.getAccountByAccountNumber(transaction.getSenderAccountNumber());
            ownsSender = sender.getCustomerId().equals(customerProfileId);
        }

        if (transaction.getReceiverAccountNumber() != null) {
            AccountResponse receiver = accountService.getAccountByAccountNumber(transaction.getReceiverAccountNumber());
            ownsReceiver = receiver.getCustomerId().equals(customerProfileId);
        }

        if (!ownsSender && !ownsReceiver) {
            throw new ForbiddenException("Access Denied: You are not authorized to view details of this transaction.");
        }
    }
}
