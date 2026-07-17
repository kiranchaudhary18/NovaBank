package com.novabank.backend.dto;

import com.novabank.backend.enums.CardNetwork;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object representing a lightweight card summary.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSummaryResponse {

    private UUID id;
    private String maskedCardNumber;
    private CardType cardType;
    private CardNetwork cardNetwork;
    private CardStatus status;
    private LocalDate expiryDate;
}
