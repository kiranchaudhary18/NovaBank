package com.novabank.backend.dto;

import com.novabank.backend.validation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing request parameters for updating a user profile.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    /** The full name of the user profile. */
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    /** Contact phone number. validated using the custom PhoneNumber validator. */
    @NotBlank(message = "Phone number is required")
    @PhoneNumber
    private String phone;
}
