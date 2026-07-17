package com.novabank.backend.service;

import com.novabank.backend.dto.StatementResponse;

/**
 * Service interface defining operations related to generating PDF statements.
 *
 * @author Senior Java Backend Architect
 */
public interface PdfStatementService {

    /**
     * Renders a professional PDF statement document containing logo headers, account summaries,
     * and a detailed ledger table.
     *
     * @param response the statement details
     * @return raw binary PDF byte array
     */
    byte[] generatePdfStatement(StatementResponse response);
}
