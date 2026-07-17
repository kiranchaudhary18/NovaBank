package com.novabank.backend.dto;

import com.novabank.backend.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a single transaction row in the bank statement.
 * Calculates credit/debit amount columns and running balances.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryResponse {

    private UUID id;
    private String transactionId;
    private String referenceNumber;
    private LocalDateTime transactionDate;
    private TransactionType transactionType;

    /** Debit amount (populated only if the transaction was a debit). */
    private BigDecimal debitAmount;

    /** Credit amount (populated only if the transaction was a credit). */
    private BigDecimal creditAmount;

    /** Running balance at the completion of this transaction. */
    private BigDecimal runningBalance;

    private String remarks;
}
