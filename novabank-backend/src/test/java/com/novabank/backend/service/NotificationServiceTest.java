package com.novabank.backend.service;

import com.novabank.backend.dto.NotificationPreferenceRequest;
import com.novabank.backend.dto.NotificationRequest;
import com.novabank.backend.dto.NotificationResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.Notification;
import com.novabank.backend.entity.NotificationPreference;
import com.novabank.backend.entity.Role;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.NotificationPriority;
import com.novabank.backend.enums.NotificationStatus;
import com.novabank.backend.enums.NotificationType;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.repository.NotificationPreferenceRepository;
import com.novabank.backend.repository.NotificationRepository;
import com.novabank.backend.repository.UserRepository;
import com.novabank.backend.service.impl.NotificationServiceImpl;
import com.novabank.backend.service.impl.TemplateServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for {@link NotificationServiceImpl}.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Spy
    private TemplateServiceImpl templateService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User sampleUser;
    private Notification sampleNotification;
    private NotificationPreference samplePreference;
    private UUID userId;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        Role customerRole = Role.builder()
                .roleName(RoleType.ROLE_CUSTOMER)
                .build();

        sampleUser = User.builder()
                .fullName("Jane Doe")
                .email("jane.doe@novabank.com")
                .role(customerRole)
                .build();
        sampleUser.setId(userId);

        sampleNotification = Notification.builder()
                .user(sampleUser)
                .title("Welcome to NovaBank!")
                .message("Dear Jane Doe, welcome to the platform.")
                .notificationType(NotificationType.IN_APP)
                .priority(NotificationPriority.NORMAL)
                .status(NotificationStatus.SENT)
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();
        sampleNotification.setId(notificationId);

        samplePreference = NotificationPreference.builder()
                .user(sampleUser)
                .emailEnabled(true)
                .inAppEnabled(true)
                .build();
        samplePreference.setId(UUID.randomUUID());
    }

    @Test
    void createNotification_Success() {
        NotificationRequest request = NotificationRequest.builder()
                .userId(userId)
                .title("Welcome to NovaBank!")
                .message("Dear Jane Doe, welcome to the platform.")
                .notificationType(NotificationType.IN_APP)
                .priority(NotificationPriority.NORMAL)
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        Mockito.when(notificationRepository.save(Mockito.any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(notificationId);
            return saved;
        });

        NotificationResponse response = notificationService.createNotification(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Welcome to NovaBank!", response.getTitle());
        Assertions.assertFalse(response.isRead());
        Mockito.verify(notificationRepository).save(Mockito.any(Notification.class));
    }

    @Test
    void getUserNotifications_Success() {
        Page<Notification> page = new PageImpl<>(List.of(sampleNotification));
        Mockito.when(notificationRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(page);

        PagedResponse<NotificationResponse> response = notificationService.getUserNotifications(
                sampleUser, 0, 10, "sentAt", "desc", null, null, null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("Welcome to NovaBank!", response.getContent().getFirst().getTitle());
    }

    @Test
    void getUnreadCount_Success() {
        Mockito.when(notificationRepository.countByUserAndIsRead(sampleUser, false)).thenReturn(5L);

        long count = notificationService.getUnreadCount(sampleUser);

        Assertions.assertEquals(5, count);
    }

    @Test
    void markAsRead_Success() {
        Mockito.when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(sampleNotification));
        Mockito.when(notificationRepository.save(Mockito.any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponse response = notificationService.markAsRead(sampleUser, notificationId);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isRead());
        Assertions.assertEquals(NotificationStatus.READ, response.getStatus());
        Mockito.verify(notificationRepository).save(sampleNotification);
    }

    @Test
    void markAsRead_Forbidden_ThrowsException() {
        User separateUser = User.builder().build();
        separateUser.setId(UUID.randomUUID());

        Mockito.when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(sampleNotification));

        Assertions.assertThrows(ForbiddenException.class, () -> notificationService.markAsRead(separateUser, notificationId));
    }

    @Test
    void markAllAsRead_Success() {
        Mockito.when(notificationRepository.findByUserAndIsRead(sampleUser, false)).thenReturn(List.of(sampleNotification));

        notificationService.markAllAsRead(sampleUser);

        Assertions.assertTrue(sampleNotification.isRead());
        Assertions.assertEquals(NotificationStatus.READ, sampleNotification.getStatus());
        Mockito.verify(notificationRepository).saveAll(Mockito.anyList());
    }

    @Test
    void getNotificationPreferences_SeedsDefault_IfEmpty() {
        Mockito.when(notificationPreferenceRepository.findByUser(sampleUser)).thenReturn(Optional.empty());
        Mockito.when(notificationPreferenceRepository.save(Mockito.any(NotificationPreference.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationPreference response = notificationService.getNotificationPreferences(sampleUser);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmailEnabled());
        Assertions.assertTrue(response.isInAppEnabled());
        Mockito.verify(notificationPreferenceRepository).save(Mockito.any(NotificationPreference.class));
    }

    @Test
    void updateNotificationPreferences_Success() {
        NotificationPreferenceRequest request = NotificationPreferenceRequest.builder()
                .emailEnabled(false)
                .inAppEnabled(true)
                .build();

        Mockito.when(notificationPreferenceRepository.findByUser(sampleUser)).thenReturn(Optional.of(samplePreference));
        Mockito.when(notificationPreferenceRepository.save(Mockito.any(NotificationPreference.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.updateNotificationPreferences(sampleUser, request);

        Assertions.assertFalse(samplePreference.isEmailEnabled());
        Assertions.assertTrue(samplePreference.isInAppEnabled());
        Mockito.verify(notificationPreferenceRepository).save(samplePreference);
    }

    @Test
    void templateService_HtmlReplacements() {
        String welcome = templateService.getWelcomeEmailTemplate("Jane Doe");
        Assertions.assertTrue(welcome.contains("Jane Doe"));
        Assertions.assertTrue(welcome.contains("Welcome to NovaBank"));
    }
}
