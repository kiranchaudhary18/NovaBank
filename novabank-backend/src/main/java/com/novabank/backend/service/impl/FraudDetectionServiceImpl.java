package com.novabank.backend.service.impl;

import com.novabank.backend.dto.FraudAlertResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.FraudAlert;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AlertStatus;
import com.novabank.backend.enums.AlertType;
import com.novabank.backend.enums.RiskLevel;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.FraudAlertRepository;
import com.novabank.backend.service.FraudDetectionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation managing risk rule evaluations and alert remediations.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final FraudAlertRepository fraudAlertRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void evaluateTransactionRules(Transaction txn) {
        log.info("Risk engine evaluating rules for transaction ID: {}", txn.getTransactionId());

        BigDecimal amount = txn.getAmount();
        Account sender = txn.getSenderAccount();
        Account receiver = txn.getReceiverAccount();

        // Rule 1: Very Large Transaction (> $10,000)
        if (amount.compareTo(new BigDecimal("10000.00")) > 0) {
            triggerAlert(
                    txn.getSenderAccount() != null ? txn.getSenderAccount().getCustomer().getUser() : null,
                    sender, txn, AlertType.LARGE_TRANSACTION, RiskLevel.HIGH, 30,
                    "Transaction amount exceeds threshold: $" + amount
            );
        }

        // Rule 2: Transfer from Frozen Account
        if (sender != null && sender.getStatus() == AccountStatus.FROZEN) {
            triggerAlert(
                    sender.getCustomer().getUser(), sender, txn, AlertType.HIGH_RISK_TRANSFER, RiskLevel.CRITICAL, 60,
                    "Declined/Flagged: Transaction initiated from a FROZEN account: " + sender.getAccountNumber()
            );
        }

        // Rule 3: Transfer to Blocked Account
        if (receiver != null && receiver.getStatus() == AccountStatus.BLOCKED) {
            triggerAlert(
                    sender != null ? sender.getCustomer().getUser() : null, sender, txn, AlertType.HIGH_RISK_TRANSFER, RiskLevel.CRITICAL, 60,
                    "Declined/Flagged: Fund transfer sent to a BLOCKED account: " + receiver.getAccountNumber()
            );
        }

        // Rule 4: Suspicious Beneficiary (transfer immediately after adding beneficiary - e.g., within 1 hour)
        if (txn.getBeneficiary() != null) {
            LocalDateTime beneficiaryCreated = txn.getBeneficiary().getCreatedAt();
            if (beneficiaryCreated != null && beneficiaryCreated.isAfter(LocalDateTime.now().minusHours(1))) {
                triggerAlert(
                        sender != null ? sender.getCustomer().getUser() : null, sender, txn, AlertType.SUSPICIOUS_BENEFICIARY, RiskLevel.HIGH, 25,
                        "Immediate transfer to newly added beneficiary: " + txn.getBeneficiary().getBeneficiaryName()
                );
            }
        }

        // Rule 5: Multiple transfers in a short time (e.g. > 3 transfers in the last 15 minutes)
        if (sender != null) {
            LocalDateTime fifteenMinsAgo = LocalDateTime.now().minusMinutes(15);
            long recentTransfers = entityManager.createQuery(
                    "SELECT COUNT(t) FROM Transaction t " +
                            "WHERE t.senderAccount = :sender " +
                            "AND t.transactionDate >= :time " +
                            "AND t.status = 'SUCCESS'", Long.class
            ).setParameter("sender", sender)
            .setParameter("time", fifteenMinsAgo)
            .getSingleResult();

            if (recentTransfers > 3) {
                triggerAlert(
                        sender.getCustomer().getUser(), sender, txn, AlertType.MULTIPLE_TRANSFERS, RiskLevel.MEDIUM, 20,
                        "High frequency transfers: " + recentTransfers + " transfers completed in under 15 minutes"
                );
            }
        }
    }

    @Override
    @Transactional
    public void evaluateAuthenticationRules(User user, boolean isSuccess, String ip, String device, String browser, String os) {
        if (user == null) return;
        log.info("Risk engine evaluating authentication rules for: {}", user.getEmail());

        LocalDateTime tenMinsAgo = LocalDateTime.now().minusMinutes(10);

        if (!isSuccess) {
            // Rule 6: Failed Login Attack (e.g. >= 5 failures in the last 10 minutes)
            long failedCount = entityManager.createQuery(
                    "SELECT COUNT(a) FROM AuditLog a " +
                            "WHERE a.user = :user " +
                            "AND a.action = 'LOGIN' " +
                            "AND a.status = 'FAILED' " +
                            "AND a.createdAt >= :time", Long.class
            ).setParameter("user", user)
            .setParameter("time", tenMinsAgo)
            .getSingleResult();

            // Count includes current failed attempt
            if (failedCount >= 4) { 
                triggerAlert(
                        user, null, null, AlertType.FAILED_LOGIN, RiskLevel.HIGH, 10,
                        "Brute-force alert: " + (failedCount + 1) + " failed login attempts detected in under 10 minutes"
                );
            }
        } else {
            // Rule 7: Logins from multiple devices in a short time (e.g. >= 3 devices in last 1 hour)
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            long distinctDevices = entityManager.createQuery(
                    "SELECT COUNT(DISTINCT a.device) FROM AuditLog a " +
                            "WHERE a.user = :user " +
                            "AND a.action = 'LOGIN' " +
                            "AND a.status = 'SUCCESS' " +
                            "AND a.createdAt >= :time", Long.class
            ).setParameter("user", user)
            .setParameter("time", oneHourAgo)
            .getSingleResult();

            if (distinctDevices >= 2) { // 2 previous + current device = 3
                triggerAlert(
                        user, null, null, AlertType.MULTIPLE_LOGIN, RiskLevel.HIGH, 15,
                        "Session hijacking alert: Logins from " + (distinctDevices + 1) + " distinct devices within 1 hour"
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FraudAlertResponse> searchFraudAlerts(
            int page, int size, String sortBy, String sortDir,
            RiskLevel riskLevel, AlertType alertType, AlertStatus status, UUID customerId
    ) {
        log.debug("Searching fraud warnings registry");
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<FraudAlert> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (riskLevel != null) {
                predicates.add(cb.equal(root.get("riskLevel"), riskLevel));
            }
            if (alertType != null) {
                predicates.add(cb.equal(root.get("alertType"), alertType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (customerId != null) {
                // Relate customer to user alerts via standard JPA subquery
                jakarta.persistence.criteria.Subquery<UUID> subquery = query.subquery(UUID.class);
                jakarta.persistence.criteria.Root<com.novabank.backend.entity.Customer> customerRoot = subquery.from(com.novabank.backend.entity.Customer.class);
                subquery.select(customerRoot.get("user").get("id"));
                subquery.where(cb.equal(customerRoot.get("id"), customerId));
                predicates.add(root.get("user").get("id").in(subquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<FraudAlert> result = fraudAlertRepository.findAll(spec, pageable);
        return new PagedResponse<>(result.map(this::convertToFraudAlertResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public FraudAlertResponse getFraudAlertById(UUID id) {
        FraudAlert alert = fraudAlertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud alert not found with ID: " + id));
        return convertToFraudAlertResponse(alert);
    }

    @Override
    @Transactional
    public FraudAlertResponse reviewFraudAlert(UUID id, String reviewer) {
        log.info("Flagging fraud alert ID: {} as UNDER_REVIEW by: {}", id, reviewer);
        FraudAlert alert = fraudAlertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud alert not found with ID: " + id));

        if (alert.getStatus() == AlertStatus.RESOLVED || alert.getStatus() == AlertStatus.FALSE_POSITIVE) {
            throw new BadRequestException("Declined: Cannot review an already resolved alert.");
        }

        alert.setStatus(AlertStatus.UNDER_REVIEW);
        alert.setReviewedBy(reviewer);
        alert.setReviewedAt(LocalDateTime.now());

        FraudAlert updated = fraudAlertRepository.save(alert);
        return convertToFraudAlertResponse(updated);
    }

    @Override
    @Transactional
    public FraudAlertResponse resolveFraudAlert(UUID id, String reviewer, AlertStatus resolutionStatus) {
        log.info("Resolving fraud alert ID: {} to {} by: {}", id, resolutionStatus, reviewer);
        FraudAlert alert = fraudAlertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fraud alert not found with ID: " + id));

        if (resolutionStatus != AlertStatus.RESOLVED && resolutionStatus != AlertStatus.FALSE_POSITIVE) {
            throw new BadRequestException("Declined: Resolution status must be RESOLVED or FALSE_POSITIVE.");
        }

        alert.setStatus(resolutionStatus);
        alert.setReviewedBy(reviewer);
        alert.setReviewedAt(LocalDateTime.now());

        FraudAlert updated = fraudAlertRepository.save(alert);
        return convertToFraudAlertResponse(updated);
    }

    // ==========================================
    // PRIVATE ALERTS GENERATION UTILITY
    // ==========================================

    private void triggerAlert(
            User user, Account account, Transaction transaction,
            AlertType type, RiskLevel risk, int score, String reason
    ) {
        log.warn("CRITICAL SECURITY ALARM TRIGGERED! Type: {}, Level: {}, Reason: '{}'", type, risk, reason);

        FraudAlert alert = FraudAlert.builder()
                .user(user)
                .account(account)
                .transaction(transaction)
                .alertType(type)
                .riskLevel(risk)
                .riskScore(score)
                .reason(reason)
                .status(AlertStatus.OPEN)
                .build();

        fraudAlertRepository.save(alert);
    }

    private FraudAlertResponse convertToFraudAlertResponse(FraudAlert alert) {
        if (alert == null) return null;
        return FraudAlertResponse.builder()
                .id(alert.getId())
                .userId(alert.getUser() != null ? alert.getUser().getId() : null)
                .userEmail(alert.getUser() != null ? alert.getUser().getEmail() : "ANONYMOUS")
                .accountId(alert.getAccount() != null ? alert.getAccount().getId() : null)
                .accountNumber(alert.getAccount() != null ? alert.getAccount().getAccountNumber() : null)
                .transactionId(alert.getTransaction() != null ? alert.getTransaction().getId() : null)
                .transactionReference(alert.getTransaction() != null ? alert.getTransaction().getTransactionId() : null)
                .alertType(alert.getAlertType())
                .riskLevel(alert.getRiskLevel())
                .riskScore(alert.getRiskScore())
                .reason(alert.getReason())
                .status(alert.getStatus())
                .reviewedBy(alert.getReviewedBy())
                .reviewedAt(alert.getReviewedAt())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
