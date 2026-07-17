package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing the summary headers of a bank statement period.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatementSummary {

    private String customerName;
    private String accountNumber;
    private String period;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
}
