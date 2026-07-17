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
 * Data Transfer Object representing serialized transaction details in API responses.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private UUID id;
    private String transactionId;
    private String referenceNumber;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String beneficiaryName;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private String currency;
    private String remarks;
    private TransactionStatus status;
    private String failureReason;
    private String initiatedBy;
    private String approvedBy;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
