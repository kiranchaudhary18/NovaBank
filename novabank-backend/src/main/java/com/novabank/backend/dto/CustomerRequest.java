package com.novabank.backend.dto;

import com.novabank.backend.enums.Gender;
import com.novabank.backend.enums.MaritalStatus;
import com.novabank.backend.validation.PhoneNumber;
import com.novabank.backend.validation.ValidAge;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object containing request parameters to create or update customer profiles.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Date of birth is required")
    @ValidAge(message = "Customer must be at least 18 years of age")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Phone number is required")
    @PhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Marital status is required")
    private MaritalStatus maritalStatus;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @NotNull(message = "Annual income is required")
    @PositiveOrZero(message = "Annual income must be a positive number or zero")
    private BigDecimal annualIncome;

    @NotBlank(message = "Nationality is required")
    private String nationality;

    @Valid
    private List<AddressRequest> addresses;
}
