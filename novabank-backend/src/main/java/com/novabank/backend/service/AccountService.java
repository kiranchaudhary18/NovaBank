package com.novabank.backend.service;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Account;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface defining bank account lifecycle and query operations.
 *
 * @author Senior Java Backend Architect
 */
public interface AccountService {

    /**
     * Opens a new bank account.
     * Enforces KYC verification constraints and primary savings account uniqueness.
     *
     * @param request account creation details
     * @return AccountResponse details DTO
     */
    AccountResponse createAccount(CreateAccountRequest request);

    /**
     * Retrieves account details by its UUID.
     *
     * @param id account UUID
     * @return AccountResponse details DTO
     */
    AccountResponse getAccountById(UUID id);

    /**
     * Retrieves account details by its unique generated account number.
     *
     * @param number account number string
     * @return AccountResponse details DTO
     */
    AccountResponse getAccountByAccountNumber(String number);

    /**
     * Lists and filters accounts dynamically with pagination and sorting.
     * Accessible by administrative roles (Admin/Employee).
     *
     * @param page zero-indexed page number
     * @param size page limit size
     * @param sortBy property parameter to sort by
     * @param sortDir sort direction (asc/desc)
     * @param type filter by AccountType (optional)
     * @param status filter by AccountStatus (optional)
     * @param openedDate filter by openedDate (optional)
     * @param accountNumber search by Account Number query (optional)
     * @param customerId search by Customer ID query (optional)
     * @return PagedResponse containing list of accounts
     */
    PagedResponse<AccountResponse> searchAccounts(
            int page, int size, String sortBy, String sortDir,
            AccountType type, AccountStatus status, LocalDate openedDate,
            String accountNumber, UUID customerId
    );

    /**
     * Updates an account's details (such as changing primary status).
     *
     * @param id account UUID
     * @param request update parameters
     * @return updated AccountResponse details DTO
     */
    AccountResponse updateAccount(UUID id, UpdateAccountRequest request);

    /**
     * Freezes a bank account, suspending debits and credits.
     *
     * @param id account UUID
     * @return updated AccountResponse details DTO
     */
    AccountResponse freezeAccount(UUID id);

    /**
     * Reactivates a frozen or blocked bank account.
     *
     * @param id account UUID
     * @return updated AccountResponse details DTO
     */
    AccountResponse activateAccount(UUID id);

    /**
     * Closes a bank account. Once closed, an account cannot be modified.
     *
     * @param id account UUID
     * @return updated AccountResponse details DTO
     */
    AccountResponse closeAccount(UUID id);

    /**
     * Designates a specific savings account as the primary savings account.
     * Disables primary flags on all other accounts owned by the same customer.
     *
     * @param id account UUID
     * @return updated AccountResponse details DTO
     */
    AccountResponse setPrimaryAccount(UUID id);

    /**
     * Lists lightweight summaries of all accounts owned by a specific customer.
     *
     * @param customerId customer profile UUID
     * @return list of account summaries
     */
    List<AccountSummaryResponse> getAccountsByCustomerId(UUID customerId);

    /**
     * Helper to map Account entity details to AccountResponse DTO.
     *
     * @param account Account persistence entity
     * @return AccountResponse DTO representation
     */
    AccountResponse convertToAccountResponse(Account account);
}
