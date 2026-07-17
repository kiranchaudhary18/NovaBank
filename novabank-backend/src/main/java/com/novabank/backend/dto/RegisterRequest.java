package com.novabank.backend.dto;

import com.novabank.backend.validation.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing user registration parameters.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    /** The full name of the user register applicant. */
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    /** Email address to use as username for authentication. */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    /** Secure password plain text, to be encrypted before saving. */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    /** Contact phone number. validated using the custom PhoneNumber validator. */
    @NotBlank(message = "Phone number is required")
    @PhoneNumber
    private String phone;

    /** Privilege role to request (optional, e.g., 'ADMIN', 'EMPLOYEE', defaults to 'CUSTOMER'). */
    private String role;
}
