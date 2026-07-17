package com.novabank.backend.dto;

import com.novabank.backend.enums.NotificationPriority;
import com.novabank.backend.enums.NotificationStatus;
import com.novabank.backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing serialized notification details in API responses.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private NotificationType notificationType;
    private NotificationPriority priority;
    private NotificationStatus status;
    private boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
}
