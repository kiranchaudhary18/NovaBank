package com.novabank.backend.repository;

import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.enums.AccountType;
import com.novabank.backend.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link Account} entity.
 * Extends {@link JpaSpecificationExecutor} to enable criteria-based paginated search queries.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {

    /**
     * Finds accounts by type and status using pagination.
     */
    Page<Account> findByAccountTypeAndStatus(AccountType accountType, AccountStatus status, Pageable pageable);

    /**
     * Finds an account by its unique generated account number.
     *
     * @param accountNumber the account number
     * @return Optional containing the found Account, or empty
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Lists all accounts owned by a specific Customer.
     *
     * @param customer customer profile entity
     * @return list of accounts
     */
    List<Account> findByCustomer(Customer customer);

    /**
     * Checks if an account exists with the given account number.
     *
     * @param accountNumber the account number to check
     * @return true if exists, false otherwise
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Finds the primary account of a specific type owned by a Customer.
     * Used for validating primary account constraints.
     *
     * @param customer customer profile entity
     * @param accountType category type (e.g. SAVINGS)
     * @param isPrimary state of the primary flag
     * @return Optional containing the primary Account, or empty
     */
    Optional<Account> findByCustomerAndAccountTypeAndIsPrimary(Customer customer, AccountType accountType, boolean isPrimary);
}
