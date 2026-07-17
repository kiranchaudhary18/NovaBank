package com.novabank.backend.service.impl;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Notification;
import com.novabank.backend.entity.NotificationPreference;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.NotificationPriority;
import com.novabank.backend.enums.NotificationStatus;
import com.novabank.backend.enums.NotificationType;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.NotificationPreferenceRepository;
import com.novabank.backend.repository.NotificationRepository;
import com.novabank.backend.repository.UserRepository;
import com.novabank.backend.service.NotificationService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation managing user notifications, unread lookups, and preferences.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("Creating notification: '{}' for user ID: {}", request.getTitle(), request.getUserId());
        User recipient = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        Notification notification = Notification.builder()
                .user(recipient)
                .title(request.getTitle())
                .message(request.getMessage())
                .notificationType(request.getNotificationType())
                .priority(request.getPriority())
                .status(NotificationStatus.SENT) // Default to SENT when successfully written to the database
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        return convertToNotificationResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<NotificationResponse> getUserNotifications(
            User user, int page, int size, String sortBy, String sortDir,
            Boolean isRead, NotificationType type, NotificationPriority priority
    ) {
        log.info("Fetching notifications for user: {}", user.getEmail());
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Notification> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Security constraint: Customer can only view their own notifications
            predicates.add(cb.equal(root.get("user").get("id"), user.getId()));

            if (isRead != null) {
                predicates.add(cb.equal(root.get("isRead"), isRead));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("notificationType"), type));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Notification> pageResult = notificationRepository.findAll(spec, pageable);
        return new PagedResponse<>(pageResult.map(this::convertToNotificationResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(User user, UUID id) {
        log.info("Marking notification ID: {} as read", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));

        // Security check
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Access Denied: You do not own this notification.");
        }

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.READ);
        
        Notification updated = notificationRepository.save(notification);
        return convertToNotificationResponse(updated);
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        log.info("Marking all notifications as read for user: {}", user.getEmail());
        List<Notification> unreadList = notificationRepository.findByUserAndIsRead(user, false);
        LocalDateTime now = LocalDateTime.now();

        for (Notification notification : unreadList) {
            notification.setRead(true);
            notification.setReadAt(now);
            notification.setStatus(NotificationStatus.READ);
        }

        notificationRepository.saveAll(unreadList); // Batch update
    }

    @Override
    @Transactional
    public void deleteNotification(User user, UUID id) {
        log.info("Deleting notification ID: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));

        // Security check
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Access Denied: You do not own this notification.");
        }

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void updateNotificationPreferences(User user, NotificationPreferenceRequest request) {
        log.info("Updating notification preferences for user: {}", user.getEmail());
        NotificationPreference preferences = getNotificationPreferences(user);

        preferences.setEmailEnabled(request.getEmailEnabled());
        preferences.setInAppEnabled(request.getInAppEnabled());

        notificationPreferenceRepository.save(preferences);
    }

    @Override
    @Transactional
    public NotificationPreference getNotificationPreferences(User user) {
        return notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> {
                    // Seed defaults if no configuration records exist
                    log.info("Seeding default notification preferences for user: {}", user.getEmail());
                    NotificationPreference defaults = NotificationPreference.builder()
                            .user(user)
                            .emailEnabled(true)
                            .inAppEnabled(true)
                            .build();
                    return notificationPreferenceRepository.save(defaults);
                });
    }

    @Override
    public NotificationResponse convertToNotificationResponse(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .priority(notification.getPriority())
                .status(notification.getStatus())
                .isRead(notification.isRead())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
