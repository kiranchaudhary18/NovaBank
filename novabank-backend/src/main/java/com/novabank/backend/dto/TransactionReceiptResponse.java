package com.novabank.backend.dto;

import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a transaction receipt configuration.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionReceiptResponse {

    private String referenceNumber;
    private String transactionId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private BigDecimal charges;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
    private TransactionStatus status;
    private String remarks;
}
