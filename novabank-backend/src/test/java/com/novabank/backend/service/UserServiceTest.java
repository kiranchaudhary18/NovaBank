package com.novabank.backend.service;

import com.novabank.backend.dto.UpdateUserRequest;
import com.novabank.backend.dto.UserResponse;
import com.novabank.backend.entity.Role;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.enums.UserStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.UserRepository;
import com.novabank.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for {@link UserServiceImpl}.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EventPublisherService eventPublisherService;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UUID sampleUserId;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        Role sampleRole = Role.builder()
                .roleName(RoleType.ROLE_CUSTOMER)
                .build();

        sampleUser = User.builder()
                .fullName("John Doe")
                .email("john.doe@novabank.com")
                .password("EncryptedPasswordHash")
                .phone("+12025550143")
                .role(sampleRole)
                .status(UserStatus.ACTIVE)
                .build();
        sampleUser.setId(sampleUserId);
    }

    @Test
    void getUserById_Success() {
        Mockito.when(userRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));

        UserResponse response = userService.getUserById(sampleUserId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(sampleUser.getFullName(), response.getFullName());
        Assertions.assertEquals(sampleUser.getEmail(), response.getEmail());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        Mockito.when(userRepository.findById(sampleUserId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(sampleUserId));
    }

    @Test
    void getUserById_SoftDeleted_ThrowsException() {
        sampleUser.setStatus(UserStatus.DELETED);
        Mockito.when(userRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(sampleUserId));
    }

    @Test
    void updateProfile_Success() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .fullName("Jane Doe")
                .phone("+12025550999")
                .build();

        Mockito.when(userRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));
        Mockito.when(userRepository.existsByPhoneAndIdNot(request.getPhone(), sampleUserId)).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateProfile(sampleUserId, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Jane Doe", response.getFullName());
        Assertions.assertEquals("+12025550999", response.getPhone());
    }

    @Test
    void updateProfile_DuplicatePhone_ThrowsException() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .fullName("Jane Doe")
                .phone("+12025550999")
                .build();

        Mockito.when(userRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));
        Mockito.when(userRepository.existsByPhoneAndIdNot(request.getPhone(), sampleUserId)).thenReturn(true);

        Assertions.assertThrows(BadRequestException.class, () -> userService.updateProfile(sampleUserId, request));
    }

    @Test
    void softDelete_Success() {
        Mockito.when(userRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.softDelete(sampleUserId);

        Assertions.assertEquals(UserStatus.DELETED, sampleUser.getStatus());
        Mockito.verify(userRepository).save(sampleUser);
    }
}
