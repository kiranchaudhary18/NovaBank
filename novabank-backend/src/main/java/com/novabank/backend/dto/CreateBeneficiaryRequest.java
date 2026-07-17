package com.novabank.backend.dto;

import com.novabank.backend.enums.RelationshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing request inputs to register a saved beneficiary.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBeneficiaryRequest {

    @NotBlank(message = "Beneficiary name is required")
    @Size(min = 2, max = 100, message = "Beneficiary name must be between 2 and 100 characters")
    private String beneficiaryName;

    @NotBlank(message = "Beneficiary account number is required")
    @Size(min = 5, max = 30, message = "Account number must be between 5 and 30 characters")
    private String beneficiaryAccountNumber;

    @NotBlank(message = "Beneficiary bank name is required")
    @Size(min = 2, max = 100, message = "Bank name must be between 2 and 100 characters")
    private String beneficiaryBankName;

    /** Regulatory standard 11-digit IFSC code verification regex. */
    @NotBlank(message = "IFSC code is required")
    @Pattern(
            regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
            message = "IFSC code must be a valid 11-digit code (e.g. NOVA0001001)"
    )
    private String beneficiaryIfscCode;

    @Size(max = 50, message = "Nickname must be less than 50 characters")
    private String nickname;

    @NotNull(message = "Relationship type is required")
    private RelationshipType relationship;

    private boolean isFavorite;
}
