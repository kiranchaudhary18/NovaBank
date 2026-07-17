package com.novabank.backend.listener;

import com.novabank.backend.entity.*;
import com.novabank.backend.enums.NotificationPriority;
import com.novabank.backend.enums.NotificationType;
import com.novabank.backend.event.*;
import com.novabank.backend.service.EmailService;
import com.novabank.backend.service.NotificationService;
import com.novabank.backend.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Event listener that catches platform events (user registration, transaction completion, etc.)
 * and dispatches notifications asynchronously based on user preferences.
 *
 * @author Senior Java Backend Architect
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final TemplateService templateService;

    @EventListener
    @Async("taskExecutor")
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = event.getUser();
        log.info("Event UserRegisteredEvent received for: {}", user.getEmail());

        NotificationPreference preference = notificationService.getNotificationPreferences(user);
        String fullName = user.getFullName();

        if (preference.isInAppEnabled()) {
            notificationService.createNotification(com.novabank.backend.dto.NotificationRequest.builder()
                    .userId(user.getId())
                    .title("Welcome to NovaBank!")
                    .message("Dear " + fullName + ", Welcome to NovaBank! Your account registration was completed successfully.")
                    .notificationType(NotificationType.IN_APP)
                    .priority(NotificationPriority.NORMAL)
                    .build());
        }

        if (preference.isEmailEnabled()) {
            String htmlContent = templateService.getWelcomeEmailTemplate(fullName);
            emailService.sendHtmlEmail(user.getEmail(), "Welcome to NovaBank", htmlContent);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        Transaction txn = event.getTransaction();
        log.info("Event TransactionCompletedEvent received for: {}", txn.getTransactionId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = txn.getTransactionDate().format(formatter);
        String amountStr = "$" + txn.getAmount();

        // 1. Notify Debited Party (Sender Account Owner)
        if (txn.getSenderAccount() != null) {
            User senderUser = txn.getSenderAccount().getCustomer().getUser();
            NotificationPreference preference = notificationService.getNotificationPreferences(senderUser);
            String fullName = senderUser.getFullName();

            String title = "Account Debited - " + txn.getTransactionType().name();
            String message = "Your account " + txn.getSenderAccount().getAccountNumber() +
                    " has been debited with " + amountStr + " for transaction ID: " + txn.getTransactionId();

            if (preference.isInAppEnabled()) {
                notificationService.createNotification(com.novabank.backend.dto.NotificationRequest.builder()
                        .userId(senderUser.getId())
                        .title(title)
                        .message(message)
                        .notificationType(NotificationType.IN_APP)
                        .priority(NotificationPriority.HIGH)
                        .build());
            }

            if (preference.isEmailEnabled()) {
                String htmlContent = templateService.getTransactionSuccessEmailTemplate(
                        fullName, txn.getReferenceNumber(), txn.getTransactionType().name(), amountStr, formattedDate
                );
                emailService.sendHtmlEmail(senderUser.getEmail(), title, htmlContent);
            }
        }

        // 2. Notify Credited Party (Receiver Account Owner)
        if (txn.getReceiverAccount() != null) {
            User receiverUser = txn.getReceiverAccount().getCustomer().getUser();
            NotificationPreference preference = notificationService.getNotificationPreferences(receiverUser);
            String fullName = receiverUser.getFullName();

            String title = "Account Credited - " + txn.getTransactionType().name();
            String message = "Your account " + txn.getReceiverAccount().getAccountNumber() +
                    " has been credited with " + amountStr + " for transaction ID: " + txn.getTransactionId();

            if (preference.isInAppEnabled()) {
                notificationService.createNotification(com.novabank.backend.dto.NotificationRequest.builder()
                        .userId(receiverUser.getId())
                        .title(title)
                        .message(message)
                        .notificationType(NotificationType.IN_APP)
                        .priority(NotificationPriority.HIGH)
                        .build());
            }

            if (preference.isEmailEnabled()) {
                String htmlContent = templateService.getTransactionSuccessEmailTemplate(
                        fullName, txn.getReferenceNumber(), txn.getTransactionType().name(), amountStr, formattedDate
                );
                emailService.sendHtmlEmail(receiverUser.getEmail(), title, htmlContent);
            }
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void handleCardIssued(CardIssuedEvent event) {
        Card card = event.getCard();
        User user = card.getCustomer().getUser();
        log.info("Event CardIssuedEvent received for user: {}", user.getEmail());

        NotificationPreference preference = notificationService.getNotificationPreferences(user);
        String fullName = user.getFullName();
        String maskedCard = card.getMaskedCardNumber();
        String typeStr = card.getCardType().name();

        String title = "Debit Card Issued";
        String message = "A new " + typeStr + " debit card (" + maskedCard + ") has been issued and linked to your account.";

        if (preference.isInAppEnabled()) {
            notificationService.createNotification(com.novabank.backend.dto.NotificationRequest.builder()
                    .userId(user.getId())
                    .title(title)
                    .message(message)
                    .notificationType(NotificationType.IN_APP)
                    .priority(NotificationPriority.NORMAL)
                    .build());
        }

        if (preference.isEmailEnabled()) {
            String htmlContent = templateService.getCardIssuedEmailTemplate(fullName, maskedCard, typeStr);
            emailService.sendHtmlEmail(user.getEmail(), title, htmlContent);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void handlePasswordChanged(PasswordChangedEvent event) {
        User user = event.getUser();
        log.info("Event PasswordChangedEvent received for: {}", user.getEmail());

        NotificationPreference preference = notificationService.getNotificationPreferences(user);
        String fullName = user.getFullName();

        String title = "Security Alert: Password Changed";
        String message = "Your NovaBank digital portal login password was changed successfully.";

        if (preference.isInAppEnabled()) {
            notificationService.createNotification(com.novabank.backend.dto.NotificationRequest.builder()
                    .userId(user.getId())
                    .title(title)
                    .message(message)
                    .notificationType(NotificationType.IN_APP)
                    .priority(NotificationPriority.CRITICAL)
                    .build());
        }

        if (preference.isEmailEnabled()) {
            String htmlContent = templateService.getPasswordChangedEmailTemplate(fullName);
            emailService.sendHtmlEmail(user.getEmail(), title, htmlContent);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void handleKycVerified(KycVerifiedEvent event) {
        User user = event.getUser();
        log.info("Event KycVerifiedEvent received for: {}", user.getEmail());

        NotificationPreference preference = notificationService.getNotificationPreferences(user);
        String fullName = user.getFullName();
        String statusStr = event.getStatus().name();

        String title = "KYC Verification Update";
        String message = "Your customer profile KYC status has been reviewed and updated to " + statusStr + ".";

        if (preference.isInAppEnabled()) {
            notificationService.createNotification(com.novabank.backend.dto.NotificationRequest.builder()
                    .userId(user.getId())
                    .title(title)
                    .message(message)
                    .notificationType(NotificationType.IN_APP)
                    .priority(NotificationPriority.HIGH)
                    .build());
        }

        if (preference.isEmailEnabled()) {
            String htmlContent = templateService.getWelcomeEmailTemplate(fullName); // Send standard Kyc verified / Welcome
            emailService.sendHtmlEmail(user.getEmail(), title, htmlContent);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void handleLoanApproved(LoanApprovedEvent event) {
        User user = event.getUser();
        log.info("Event LoanApprovedEvent received for: {}", user.getEmail());

        NotificationPreference preference = notificationService.getNotificationPreferences(user);
        String fullName = user.getFullName();
        String amountStr = "$" + event.getAmount();

        String title = "Loan Application Approved";
        String message = "Your NovaBank credit loan application has been approved for " + amountStr + ".";

        if (preference.isInAppEnabled()) {
            notificationService.createNotification(com.novabank.backend.dto.NotificationRequest.builder()
                    .userId(user.getId())
                    .title(title)
                    .message(message)
                    .notificationType(NotificationType.IN_APP)
                    .priority(NotificationPriority.HIGH)
                    .build());
        }

        if (preference.isEmailEnabled()) {
            String htmlContent = templateService.getLoanApprovedEmailTemplate(fullName, amountStr);
            emailService.sendHtmlEmail(user.getEmail(), title, htmlContent);
        }
    }
}
