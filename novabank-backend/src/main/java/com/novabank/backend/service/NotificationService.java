package com.novabank.backend.service;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.Notification;
import com.novabank.backend.entity.NotificationPreference;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.NotificationPriority;
import com.novabank.backend.enums.NotificationType;

import java.util.UUID;

/**
 * Service interface defining notification management operations, histories, and preference lookups.
 *
 * @author Senior Java Backend Architect
 */
public interface NotificationService {

    /**
     * Persists a new notification alert entry in the system database.
     *
     * @param request creation parameters
     * @return NotificationResponse details DTO
     */
    NotificationResponse createNotification(NotificationRequest request);

    /**
     * Lists, filters, and searches notifications paginated for a user.
     *
     * @param user authenticated caller
     * @param page zero-indexed page number
     * @param size page limit size
     * @param sortBy sort property key
     * @param sortDir sort direction (asc/desc)
     * @param isRead filter by read status (optional)
     * @param type filter by delivery channel type (optional)
     * @param priority filter by priority level (optional)
     * @return PagedResponse containing NotificationResponse details
     */
    PagedResponse<NotificationResponse> getUserNotifications(
            User user, int page, int size, String sortBy, String sortDir,
            Boolean isRead, NotificationType type, NotificationPriority priority
    );

    /**
     * Retrieves the count of unread notifications for a user.
     *
     * @param user target user
     * @return count of unread notifications
     */
    long getUnreadCount(User user);

    /**
     * Marks a specific notification as READ.
     *
     * @param user authenticated card owner
     * @param id notification UUID
     * @return updated NotificationResponse details
     */
    NotificationResponse markAsRead(User user, UUID id);

    /**
     * Marks all unread notifications for a user as READ.
     *
     * @param user authenticated card owner
     */
    void markAllAsRead(User user);

    /**
     * Permanently deletes a notification entry from the database.
     *
     * @param user authenticated owner
     * @param id notification UUID
     */
    void deleteNotification(User user, UUID id);

    /**
     * Updates notification preferences (email/in-app switches) configured by a user.
     *
     * @param user authenticated card owner
     * @param request preferences configurations
     */
    void updateNotificationPreferences(User user, NotificationPreferenceRequest request);

    /**
     * Retrieves the notification channel preferences configured by a user.
     * Automatically seeds default preferences (both enabled) if none exist.
     *
     * @param user target user
     * @return NotificationPreference details
     */
    NotificationPreference getNotificationPreferences(User user);

    /**
     * Helper to map Notification entity details to NotificationResponse DTO.
     *
     * @param notification Notification entity
     * @return NotificationResponse DTO representation
     */
    NotificationResponse convertToNotificationResponse(Notification notification);
}
