package com.novabank.backend.repository;

import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link Transaction} entity.
 * Extends {@link JpaSpecificationExecutor} to enable criteria-based paginated search queries.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    /**
     * Finds a transaction by its unique generated transaction ID.
     *
     * @param transactionId the transaction ID
     * @return Optional containing the transaction, or empty
     */
    Optional<Transaction> findByTransactionId(String transactionId);

    /**
     * Finds a transaction by its unique reference number.
     *
     * @param referenceNumber the reference number
     * @return Optional containing the transaction, or empty
     */
    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    /**
     * Lists paginated transactions associated with an account (either as sender or receiver).
     *
     * @param sender the sender account
     * @param receiver the receiver account
     * @param pageable pagination options
     * @return page of transactions
     */
    Page<Transaction> findBySenderAccountOrReceiverAccount(Account sender, Account receiver, Pageable pageable);

    /**
     * Sums the total cash withdrawals made by an account since a specific start timestamp.
     * Used for daily withdrawal limit checks.
     *
     * @param account the account to check
     * @param start start timestamp (usually start of current day)
     * @return sum of withdrawal amounts
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.senderAccount = :account " +
           "AND t.transactionType = 'WITHDRAW' " +
           "AND t.status = 'SUCCESS' " +
           "AND t.transactionDate >= :start")
    BigDecimal calculateDailyWithdrawalTotal(@Param("account") Account account, @Param("start") LocalDateTime start);

    /**
     * Sums the total fund transfers initiated by an account since a specific start timestamp.
     * Used for daily transfer limit checks.
     *
     * @param account the account to check
     * @param start start timestamp (usually start of current day)
     * @return sum of transfer amounts
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.senderAccount = :account " +
           "AND t.transactionType IN ('TRANSFER', 'BENEFICIARY_TRANSFER', 'INTERNAL_TRANSFER') " +
           "AND t.status = 'SUCCESS' " +
           "AND t.transactionDate >= :start")
    BigDecimal calculateDailyTransferTotal(@Param("account") Account account, @Param("start") LocalDateTime start);
}
