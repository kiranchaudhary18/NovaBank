package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing the complete response for a bank statement request.
 * Contains both summary metrics and transaction list details.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatementResponse {

    private StatementSummary summary;
    private List<TransactionHistoryResponse> transactions;
}
