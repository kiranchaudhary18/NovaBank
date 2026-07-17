package com.novabank.backend.service.impl;

import com.novabank.backend.dto.DashboardResponse;
import com.novabank.backend.service.DashboardService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Service implementation compiling high-level KPIs and statistics for administrative dashboards.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStats() {
        log.info("Compiling Admin Dashboard high-level KPIs");

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        long totalCustomers = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.status != 'DELETED'", Long.class
        ).getSingleResult();

        long activeCustomers = entityManager.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE c.status = 'ACTIVE'", Long.class
        ).getSingleResult();

        long pendingKyc = entityManager.createQuery(
                "SELECT COUNT(k) FROM Kyc k WHERE k.verificationStatus = 'PENDING'", Long.class
        ).getSingleResult();

        long totalAccounts = entityManager.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.status != 'CLOSED'", Long.class
        ).getSingleResult();

        BigDecimal totalBalance = entityManager.createQuery(
                "SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.status != 'CLOSED'", BigDecimal.class
        ).getSingleResult();

        long totalTxnsToday = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :today", Long.class
        ).setParameter("today", startOfToday)
        .getSingleResult();

        BigDecimal volumeToday = entityManager.createQuery(
                "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
                        "WHERE t.transactionDate >= :today AND t.status = 'SUCCESS'", BigDecimal.class
        ).setParameter("today", startOfToday)
        .getSingleResult();

        // Calculate dynamic transaction charges (e.g. $1.50 per successful transfer)
        long transfersCount = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t " +
                        "WHERE t.transactionDate >= :today " +
                        "AND t.transactionType IN ('TRANSFER', 'BENEFICIARY_TRANSFER', 'INTERNAL_TRANSFER') " +
                        "AND t.status = 'SUCCESS'", Long.class
        ).setParameter("today", startOfToday)
        .getSingleResult();
        BigDecimal txnCharges = new BigDecimal(transfersCount).multiply(new BigDecimal("1.50"));

        // Stubs for excluded Loan entity data
        BigDecimal mockOutstandingLoans = new BigDecimal("75000.00");
        BigDecimal mockRevenueToday = new BigDecimal("450.00").add(txnCharges);

        return DashboardResponse.builder()
                .totalCustomers(totalCustomers)
                .activeCustomers(activeCustomers)
                .pendingKycCount(pendingKyc)
                .totalAccounts(totalAccounts)
                .totalBalance(totalBalance)
                .totalTransactionsToday(totalTxnsToday)
                .totalTransactionVolumeToday(volumeToday)
                .totalOutstandingLoans(mockOutstandingLoans)
                .totalRevenueToday(mockRevenueToday)
                .build();
    }
}
