package com.novabank.backend.dto;

import com.novabank.backend.enums.CardNetwork;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing serialized card details in API responses.
 * Follows strict compliance rules: never exposes PIN or CVV.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {

    private UUID id;
    private String cardNumber;
    private String maskedCardNumber;
    private UUID customerId;
    private String accountNumber;
    private String cardHolderName;
    private CardType cardType;
    private CardNetwork cardNetwork;
    private LocalDate expiryDate;
    private BigDecimal dailyLimit;
    private BigDecimal onlineLimit;
    private BigDecimal atmLimit;
    private boolean contactlessEnabled;
    private boolean internationalEnabled;
    private CardStatus status;
    private LocalDate issuedDate;
    private LocalDate activatedDate;
    private int replacementCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
