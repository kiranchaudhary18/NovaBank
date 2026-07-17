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
 * Data Transfer Object representing request inputs to update a saved beneficiary.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBeneficiaryRequest {

    @NotBlank(message = "Beneficiary name is required")
    @Size(min = 2, max = 100, message = "Beneficiary name must be between 2 and 100 characters")
    private String beneficiaryName;

    @NotBlank(message = "Beneficiary bank name is required")
    @Size(min = 2, max = 100, message = "Bank name must be between 2 and 100 characters")
    private String beneficiaryBankName;

    @NotBlank(message = "IFSC code is required")
    @Pattern(
            regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
            message = "IFSC code must be a valid 11-digit code"
    )
    private String beneficiaryIfscCode;

    @Size(max = 50, message = "Nickname must be less than 50 characters")
    private String nickname;

    @NotNull(message = "Relationship type is required")
    private RelationshipType relationship;

    private Boolean isFavorite;
}
