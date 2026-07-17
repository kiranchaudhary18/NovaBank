package com.novabank.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object representing parameters to update card spending limits.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCardLimitRequest {

    @NotNull(message = "Card ID is required")
    private UUID cardId;

    @NotNull(message = "Daily limit is required")
    @PositiveOrZero(message = "Daily limit must be zero or positive")
    private BigDecimal dailyLimit;

    @NotNull(message = "Online limit is required")
    @PositiveOrZero(message = "Online limit must be zero or positive")
    private BigDecimal onlineLimit;

    @NotNull(message = "ATM withdrawal limit is required")
    @PositiveOrZero(message = "ATM limit must be zero or positive")
    private BigDecimal atmLimit;
}
