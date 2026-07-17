package com.novabank.backend.dto;

import com.novabank.backend.enums.CardNetwork;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing parameters to request a virtual debit card.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualCardRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Card network is required")
    private CardNetwork cardNetwork;

    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 digits")
    private String pin;
}
