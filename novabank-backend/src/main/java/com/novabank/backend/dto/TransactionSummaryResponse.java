package com.novabank.backend.dto;

import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a lightweight bank transaction summary.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSummaryResponse {

    private UUID id;
    private String transactionId;
    private String referenceNumber;
    private TransactionType transactionType;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime transactionDate;
}
