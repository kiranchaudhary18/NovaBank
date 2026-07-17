package com.novabank.backend.dto;

import com.novabank.backend.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object representing parameters to open a new bank account.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    /** The unique Customer Profile ID opening this account. */
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    /** Category type (e.g. SAVINGS). */
    @NotNull(message = "Account type is required")
    private AccountType accountType;

    /** Currency code representation (e.g., USD, INR). */
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    @Builder.Default
    private String currency = "USD";

    /** Identification branch code identifier. */
    @NotBlank(message = "Branch code is required")
    private String branchCode;

    /** Flag indicating if this should be the primary savings account. */
    private boolean isPrimary;

    /** Optional initial deposit balance amount. Defaults to 0. */
    @PositiveOrZero(message = "Initial deposit amount must be positive or zero")
    @Builder.Default
    private BigDecimal initialBalance = BigDecimal.ZERO;
}
