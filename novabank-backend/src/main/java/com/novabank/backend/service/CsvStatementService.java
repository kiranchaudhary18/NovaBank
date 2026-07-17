package com.novabank.backend.service;

import com.novabank.backend.dto.StatementResponse;

/**
 * Service interface defining operations related to generating CSV statements.
 *
 * @author Senior Java Backend Architect
 */
public interface CsvStatementService {

    /**
     * Serializes statement fields and transaction lists into standard downloadable CSV byte stream format.
     *
     * @param response statement details
     * @return raw binary CSV byte array
     */
    byte[] generateCsvStatement(StatementResponse response);
}
