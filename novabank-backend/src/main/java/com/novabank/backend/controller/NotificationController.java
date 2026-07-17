package com.novabank.backend.controller;

import com.novabank.backend.dto.NotificationPreferenceRequest;
import com.novabank.backend.dto.NotificationResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.NotificationPriority;
import com.novabank.backend.enums.NotificationType;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.NotificationService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller exposing REST API endpoints for Notification Histories & Preferences.
 * Path mapping: "/api/v1/notifications". Protected by stateless JWT authorizations.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification & Preference Module", description = "APIs to query unread/read alert logs and update communication channel settings")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Endpoint to list user notification records (Paginated & Filtered).
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get notifications list (Paginated)", description = "Retrieves paginated notifications history for the authenticated user. Customers can only view their own notifications.")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getUserNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sentAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) NotificationPriority priority
    ) {
        log.info("Request for notifications list by user: {}", user.getEmail());
        PagedResponse<NotificationResponse> response = notificationService.getUserNotifications(
                user, page, size, sortBy, sortDir, isRead, type, priority
        );
        return ResponseUtil.success("Notifications list retrieved successfully.", response);
    }

    /**
     * Endpoint to fetch count of unread notifications.
     */
    @GetMapping("/unread")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get unread count", description = "Retrieves total count of unread notifications for the authenticated user.")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal User user
    ) {
        log.info("Request for unread notifications count by user: {}", user.getEmail());
        long count = notificationService.getUnreadCount(user);
        return ResponseUtil.success("Unread notifications count retrieved successfully.", count);
    }

    /**
     * Endpoint to mark a single notification as read.
     */
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @Operation(summary = "Mark notification as read", description = "Marks a single unread notification as read. Restricted to the notification recipient owner.")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Request to mark notification ID: {} as read by user: {}", id, user.getEmail());
        NotificationResponse response = notificationService.markAsRead(user, id);
        return ResponseUtil.success("Notification marked as read successfully.", response);
    }

    /**
     * Endpoint to mark all user notifications as read.
     */
    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @Operation(summary = "Mark all notifications as read", description = "Marks all unread notifications for the authenticated user as read.")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User user
    ) {
        log.info("Request to mark all notifications as read by user: {}", user.getEmail());
        notificationService.markAllAsRead(user);
        return ResponseUtil.success("All notifications marked as read.", null);
    }

    /**
     * Endpoint to delete a notification record.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @Operation(summary = "Delete notification", description = "Permanently deletes a notification from user history. Restricted to the notification recipient owner.")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Request to delete notification ID: {} by user: {}", id, user.getEmail());
        notificationService.deleteNotification(user, id);
        return ResponseUtil.success("Notification deleted successfully.", null);
    }

    /**
     * Endpoint to update notification preferences.
     */
    @PutMapping("/preferences")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @Operation(summary = "Update notification preferences", description = "Modifies user switches to enable or disable email and in-app channels.")
    public ResponseEntity<ApiResponse<Void>> updatePreferences(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody NotificationPreferenceRequest request
    ) {
        log.info("Request to update notification preferences by user: {}", user.getEmail());
        notificationService.updateNotificationPreferences(user, request);
        return ResponseUtil.success("Notification preferences updated successfully.", null);
    }
}
