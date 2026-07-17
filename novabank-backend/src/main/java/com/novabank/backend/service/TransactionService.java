package com.novabank.backend.service;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service interface defining general transaction management, deposits, withdrawals,
 * receipt views, and paginated criteria-based lookups.
 *
 * @author Senior Java Backend Architect
 */
public interface TransactionService {

    /**
     * Executes a cash deposit into a customer account.
     * Enforces limit checks and updates the account balance.
     *
     * @param user initiator user (can be the customer themselves or employee teller)
     * @param request deposit request details
     * @return TransactionReceiptResponse detailing execution results
     */
    TransactionReceiptResponse deposit(User user, DepositRequest request);

    /**
     * Executes a cash withdrawal from a customer account.
     * Enforces single transaction, daily limits, and minimum balance checks.
     *
     * @param user initiator user (owner customer or employee teller)
     * @param request withdrawal request details
     * @return TransactionReceiptResponse detailing execution results
     */
    TransactionReceiptResponse withdraw(User user, WithdrawRequest request);

    /**
     * Retrieves details of a specific transaction by its UUID.
     *
     * @param id transaction UUID
     * @return TransactionResponse DTO
     */
    TransactionResponse getTransactionById(UUID id);

    /**
     * Retrieves details of a specific transaction by its unique reference number.
     *
     * @param referenceNumber reference number string
     * @return TransactionResponse DTO
     */
    TransactionResponse getTransactionByReference(String referenceNumber);

    /**
     * Lists paginated transaction records where the specified account is either the sender or receiver.
     *
     * @param accountNumber target bank account number
     * @param page zero-indexed page number
     * @param size page limit size
     * @param sortBy parameter key to sort by
     * @param sortDir sort direction (asc/desc)
     * @return PagedResponse containing transaction responses
     */
    PagedResponse<TransactionResponse> getTransactionsByAccount(String accountNumber, int page, int size, String sortBy, String sortDir);

    /**
     * Searches and filters transactions dynamically using paginated specifications.
     *
     * @param page zero-indexed page number
     * @param size page limit size
     * @param sortBy parameter key to sort by
     * @param sortDir sort direction (asc/desc)
     * @param referenceNumber search query reference match (optional)
     * @param transactionId search query ID match (optional)
     * @param senderAccountNumber search query sender match (optional)
     * @param receiverAccountNumber search query receiver match (optional)
     * @param minAmount filter by minimum amount range (optional)
     * @param maxAmount filter by maximum amount range (optional)
     * @param status filter by status (optional)
     * @param type filter by type (optional)
     * @param start filter by start date range (optional)
     * @param end filter by end date range (optional)
     * @return PagedResponse containing matching transaction responses
     */
    PagedResponse<TransactionResponse> searchTransactions(
            int page, int size, String sortBy, String sortDir,
            String referenceNumber, String transactionId,
            String senderAccountNumber, String receiverAccountNumber,
            BigDecimal minAmount, BigDecimal maxAmount,
            TransactionStatus status, TransactionType type,
            LocalDateTime start, LocalDateTime end
    );

    /**
     * Helper to map Transaction entity details to TransactionResponse DTO.
     *
     * @param transaction Transaction persistence entity
     * @return TransactionResponse DTO representation
     */
    TransactionResponse convertToTransactionResponse(Transaction transaction);
}
