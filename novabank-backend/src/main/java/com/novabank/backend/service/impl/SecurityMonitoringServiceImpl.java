package com.novabank.backend.service.impl;

import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.service.AuditService;
import com.novabank.backend.service.FraudDetectionService;
import com.novabank.backend.service.SecurityMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service implementation forwarding security monitor logs to the audit service and evaluations rules.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityMonitoringServiceImpl implements SecurityMonitoringService {

    private final AuditService auditService;
    private final FraudDetectionService fraudDetectionService;

    @Override
    public void monitorLogin(User user, boolean success, String ip, String device, String browser, String os) {
        log.info("Monitoring authentication login attempt for user: {}", user != null ? user.getEmail() : "ANONYMOUS");
        
        String description = success ? "User logged in successfully" : "User login attempt failed due to invalid credentials";
        
        auditService.logAction(
                user, "LOGIN", "AUTHENTICATION", success ? "SUCCESS" : "FAILED", description,
                "POST", "/auth/login", ip, device, browser, os
        );

        fraudDetectionService.evaluateAuthenticationRules(user, success, ip, device, browser, os);
    }

    @Override
    public void monitorTransaction(Transaction txn) {
        log.info("Monitoring completed transaction ID: {}", txn.getTransactionId());
        
        User initiator = txn.getSenderAccount() != null ? txn.getSenderAccount().getCustomer().getUser() : null;
        
        auditService.logAction(
                initiator, txn.getTransactionType().name(), "TRANSACTIONS", "SUCCESS",
                "Transaction completed successfully. Ref: " + txn.getReferenceNumber() + " | Amount: $" + txn.getAmount(),
                null, null, null, null, null, null
        );

        fraudDetectionService.evaluateTransactionRules(txn);
    }

    @Override
    public void monitorPasswordChange(User user, String ip) {
        log.info("Monitoring password updates for user: {}", user.getEmail());
        
        auditService.logAction(
                user, "PASSWORD_CHANGED", "SECURITY", "SUCCESS", "Login password was changed successfully",
                "PATCH", "/users/change-password", ip, null, null, null
        );
    }

    @Override
    public void monitorKycUpdate(User user, String details) {
        log.info("Monitoring KYC audits review: {}", details);
        
        auditService.logAction(
                user, "KYC_VERIFICATION", "CUSTOMER_KYC", "SUCCESS", "KYC verification reviewed. Outcome: " + details,
                null, null, null, null, null, null
        );
    }

    @Override
    public void monitorLoanApproval(User user, BigDecimal amount) {
        log.info("Monitoring loan credit disbursements: {}", amount);
        
        auditService.logAction(
                user, "LOAN_APPROVED", "LOANS", "SUCCESS", "Credit application approved for amount: $" + amount,
                null, null, null, null, null, null
        );
    }

    @Override
    public void monitorCardBlock(User user, String cardDetails) {
        log.info("Monitoring card block locks action: {}", cardDetails);
        
        auditService.logAction(
                user, "CARD_BLOCKED", "CARDS", "SUCCESS", "Debit card blocked permanently: " + cardDetails,
                null, null, null, null, null, null
        );
    }
}
