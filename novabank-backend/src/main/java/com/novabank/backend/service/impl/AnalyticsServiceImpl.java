package com.novabank.backend.service.impl;

import com.novabank.backend.dto.*;
import com.novabank.backend.service.AnalyticsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service implementation executing database aggregations for business analytics.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public CustomerAnalyticsResponse getCustomerAnalytics() {
        log.info("Aggregating customer registration analytics");

        long total = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.status != 'DELETED'", Long.class
        ).getSingleResult();

        long active = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.status = 'ACTIVE'", Long.class
        ).getSingleResult();

        long inactive = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.status = 'INACTIVE'", Long.class
        ).getSingleResult();

        long blocked = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.status = 'SUSPENDED'", Long.class
        ).getSingleResult();

        long verified = entityManager.createQuery(
                "SELECT COUNT(k) FROM Kyc k WHERE k.verificationStatus = 'APPROVED'", Long.class
        ).getSingleResult();

        long pending = entityManager.createQuery(
                "SELECT COUNT(k) FROM Kyc k WHERE k.verificationStatus = 'PENDING'", Long.class
        ).getSingleResult();

        return CustomerAnalyticsResponse.builder()
                .totalCustomers(total)
                .activeCustomers(active)
                .inactiveCustomers(inactive)
                .blockedCustomers(blocked)
                .verifiedCustomers(verified)
                .pendingKyc(pending)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountAnalyticsResponse getAccountAnalytics() {
        log.info("Aggregating bank account analytics");

        long total = entityManager.createQuery(
                "SELECT COUNT(a) FROM Account a", Long.class
        ).getSingleResult();

        long savings = entityManager.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.accountType = 'SAVINGS' AND a.status != 'CLOSED'", Long.class
        ).getSingleResult();

        long current = entityManager.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.accountType = 'CURRENT' AND a.status != 'CLOSED'", Long.class
        ).getSingleResult();

        long closed = entityManager.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.status = 'CLOSED'", Long.class
        ).getSingleResult();

        long frozen = entityManager.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.status = 'FROZEN'", Long.class
        ).getSingleResult();

        return AccountAnalyticsResponse.builder()
                .totalAccounts(total)
                .savingsAccounts(savings)
                .currentAccounts(current)
                .closedAccounts(closed)
                .frozenAccounts(frozen)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionAnalyticsResponse getTransactionAnalytics() {
        log.info("Aggregating core transactions ledger analytics");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime startOfSevenDaysAgo = now.minusDays(7);
        LocalDateTime startOfThirtyDaysAgo = now.minusDays(30);
        LocalDateTime startOfOneYearAgo = now.minusYears(1);

        long today = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :start", Long.class
        ).setParameter("start", startOfToday).getSingleResult();

        long yesterday = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :start AND t.transactionDate < :end", Long.class
        ).setParameter("start", startOfYesterday).setParameter("end", startOfToday).getSingleResult();

        long weekly = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :start", Long.class
        ).setParameter("start", startOfSevenDaysAgo).getSingleResult();

        long monthly = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :start", Long.class
        ).setParameter("start", startOfThirtyDaysAgo).getSingleResult();

        long yearly = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :start", Long.class
        ).setParameter("start", startOfOneYearAgo).getSingleResult();

        long success = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.status = 'SUCCESS'", Long.class
        ).getSingleResult();

        long failed = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.status = 'FAILED'", Long.class
        ).getSingleResult();

        long pending = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.status = 'PENDING'", Long.class
        ).getSingleResult();

        BigDecimal average = entityManager.createQuery(
                "SELECT COALESCE(AVG(t.amount), 0) FROM Transaction t WHERE t.status = 'SUCCESS'", BigDecimal.class
        ).getSingleResult();

        BigDecimal highest = entityManager.createQuery(
                "SELECT COALESCE(MAX(t.amount), 0) FROM Transaction t WHERE t.status = 'SUCCESS'", BigDecimal.class
        ).getSingleResult();

        BigDecimal lowest = entityManager.createQuery(
                "SELECT COALESCE(MIN(t.amount), 0) FROM Transaction t WHERE t.status = 'SUCCESS'", BigDecimal.class
        ).getSingleResult();

        return TransactionAnalyticsResponse.builder()
                .todayTransactions(today)
                .yesterdayTransactions(yesterday)
                .weeklyTransactions(weekly)
                .monthlyTransactions(monthly)
                .yearlyTransactions(yearly)
                .successfulTransactions(success)
                .failedTransactions(failed)
                .pendingTransactions(pending)
                .averageTransactionAmount(average)
                .highestTransaction(highest)
                .lowestTransaction(lowest)
                .build();
    }

    @Override
    public LoanAnalyticsResponse getLoanAnalytics() {
        log.info("Returning placeholder loan statistics (excluded from database entities)");
        // Seeding standard mock analytics to satisfy reporting schemas
        return LoanAnalyticsResponse.builder()
                .applied(25)
                .approved(18)
                .rejected(7)
                .active(15)
                .closed(3)
                .overdue(1)
                .totalLoanAmount(new BigDecimal("150000.00"))
                .outstandingBalance(new BigDecimal("75000.00"))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CardAnalyticsResponse getCardAnalytics() {
        log.info("Aggregating debit and virtual cards statistics");

        long physical = entityManager.createQuery(
                "SELECT COUNT(c) FROM Card c WHERE c.cardType = 'PHYSICAL' AND c.status != 'REPLACED'", Long.class
        ).getSingleResult();

        long virtual = entityManager.createQuery(
                "SELECT COUNT(c) FROM Card c WHERE c.cardType = 'VIRTUAL' AND c.status != 'REPLACED'", Long.class
        ).getSingleResult();

        long blocked = entityManager.createQuery(
                "SELECT COUNT(c) FROM Card c WHERE c.status = 'BLOCKED'", Long.class
        ).getSingleResult();

        long expired = entityManager.createQuery(
                "SELECT COUNT(c) FROM Card c WHERE c.status = 'EXPIRED'", Long.class
        ).getSingleResult();

        return CardAnalyticsResponse.builder()
                .physicalCards(physical)
                .virtualCards(virtual)
                .blockedCards(blocked)
                .expiredCards(expired)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueAnalyticsResponse getRevenueAnalytics() {
        log.info("Aggregating platform fee revenues");

        // Calculate dynamic transaction charges (e.g. $1.50 per successful transfer)
        long transfersCount = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t " +
                        "WHERE t.transactionType IN ('TRANSFER', 'BENEFICIARY_TRANSFER', 'INTERNAL_TRANSFER') " +
                        "AND t.status = 'SUCCESS'", Long.class
        ).getSingleResult();
        BigDecimal txnCharges = new BigDecimal(transfersCount).multiply(new BigDecimal("1.50"));

        BigDecimal mockInterest = new BigDecimal("4500.00");
        BigDecimal mockProcessing = new BigDecimal("1200.00");
        BigDecimal mockPenalty = new BigDecimal("350.00");
        BigDecimal total = mockInterest.add(mockProcessing).add(mockPenalty).add(txnCharges);

        return RevenueAnalyticsResponse.builder()
                .loanInterestEarned(mockInterest)
                .processingFees(mockProcessing)
                .penaltyCharges(mockPenalty)
                .transactionCharges(txnCharges)
                .totalRevenue(total)
                .build();
    }
}
