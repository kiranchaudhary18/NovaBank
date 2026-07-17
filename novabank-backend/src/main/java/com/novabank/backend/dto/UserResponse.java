package com.novabank.backend.dto;

import com.novabank.backend.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a sanitized user profile response.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    /** Unique identifier of the user record. */
    private UUID id;

    /** Full name of the user. */
    private String fullName;

    /** Login email address of the user. */
    private String email;

    /** User contact phone number. */
    private String phone;

    /** Assigned role name (e.g. ROLE_CUSTOMER, ROLE_ADMIN). */
    private String role;

    /** Account operational state. */
    private UserStatus status;

    /** Flag indicating email verification status. */
    private boolean emailVerified;

    /** Audit fields. */
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
