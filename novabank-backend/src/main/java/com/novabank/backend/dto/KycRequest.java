package com.novabank.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing request inputs to submit customer KYC records.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycRequest {

    /** Standard 12-digit Indian Aadhaar card number. Starts with 2-9. */
    @NotBlank(message = "Aadhaar number is required")
    @Pattern(
            regexp = "^[2-9]{1}[0-9]{3}[0-9]{4}[0-9]{4}$",
            message = "Aadhaar number must be a valid 12-digit number (e.g. starting with digits 2-9)"
    )
    private String aadhaarNumber;

    /** Standard 10-character Permanent Account Number (PAN). */
    @NotBlank(message = "PAN card number is required")
    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "PAN must be a valid 10-character alphanumeric format (e.g., ABCDE1234F)"
    )
    private String panNumber;

    private String passportNumber;

    private String drivingLicenseNumber;
}
