package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object representing a lightweight Mini Statement response (last 10 transactions).
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiniStatementResponse {

    private String accountNumber;
    private String customerName;
    private BigDecimal balance;
    private String currency;
    private List<TransactionHistoryResponse> transactions;
}
