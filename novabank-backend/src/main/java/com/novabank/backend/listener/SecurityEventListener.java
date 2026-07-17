package com.novabank.backend.listener;

import com.novabank.backend.entity.User;
import com.novabank.backend.event.*;
import com.novabank.backend.repository.UserRepository;
import com.novabank.backend.service.AuditService;
import com.novabank.backend.service.SecurityMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Event listener catching core banking and Spring Security authentication events.
 * Executes asynchronously on background thread executors to prevent blocking main transactions threads.
 *
 * @author Senior Java Backend Architect
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityEventListener {

    private final SecurityMonitoringService securityMonitoringService;
    private final AuditService auditService;
    private final UserRepository userRepository;

    @EventListener
    @Async("taskExecutor")
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = event.getUser();
        log.info("Security monitor tracking registration for: {}", user.getEmail());
        auditService.logAction(
                user, "USER_REGISTRATION", "AUTHENTICATION", "SUCCESS",
                "User profile registration completed successfully",
                null, null, null, null, null, null
        );
    }

    @EventListener
    @Async("taskExecutor")
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Security monitor tracking transaction: {}", event.getTransaction().getTransactionId());
        securityMonitoringService.monitorTransaction(event.getTransaction());
    }

    @EventListener
    @Async("taskExecutor")
    public void handleCardIssued(CardIssuedEvent event) {
        User user = event.getCard().getCustomer().getUser();
        log.info("Security monitor tracking card issue for user: {}", user.getEmail());
        auditService.logAction(
                user, "CARD_ISSUED", "CARDS_MANAGEMENT", "SUCCESS",
                "A new " + event.getCard().getCardType() + " card was issued. Masked: " + event.getCard().getMaskedCardNumber(),
                null, null, null, null, null, null
        );
    }

    @EventListener
    @Async("taskExecutor")
    public void handlePasswordChanged(PasswordChangedEvent event) {
        User user = event.getUser();
        log.info("Security monitor tracking password credentials update for user: {}", user.getEmail());
        securityMonitoringService.monitorPasswordChange(user, "127.0.0.1");
    }

    @EventListener
    @Async("taskExecutor")
    public void handleKycVerified(KycVerifiedEvent event) {
        User user = event.getUser();
        log.info("Security monitor tracking KYC check review for: {}", user.getEmail());
        securityMonitoringService.monitorKycUpdate(user, event.getStatus().name());
    }

    @EventListener
    @Async("taskExecutor")
    public void handleLoanApproved(LoanApprovedEvent event) {
        User user = event.getUser();
        log.info("Security monitor tracking loan credit approval for: {}", user.getEmail());
        securityMonitoringService.monitorLoanApproval(user, event.getAmount());
    }

    // ==========================================
    // SPRING SECURITY INTERCEPT HANDLERS
    // ==========================================

    @EventListener
    @Async("taskExecutor")
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            log.info("Security monitor intercepted successful authentication for user: {}", user.getEmail());
            securityMonitoringService.monitorLogin(user, true, "127.0.0.1", "Web Client", "Chrome", "Windows");
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String email = event.getAuthentication().getName();
        log.warn("Security monitor intercepted failed authentication attempt for email: {}", email);
        userRepository.findByEmail(email).ifPresent(user -> {
            securityMonitoringService.monitorLogin(user, false, "127.0.0.1", "Web Client", "Chrome", "Windows");
        });
    }
}
