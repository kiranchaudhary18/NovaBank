package com.novabank.backend.service;

import com.novabank.backend.dto.TransactionReceiptResponse;
import com.novabank.backend.entity.Transaction;

/**
 * Service interface defining transaction receipt generation operations.
 *
 * @author Senior Java Backend Architect
 */
public interface ReceiptService {

    /**
     * Maps transaction execution state and properties into a structured customer receipt payload.
     *
     * @param transaction final executed transaction entity
     * @return TransactionReceiptResponse DTO representation
     */
    TransactionReceiptResponse generateReceipt(Transaction transaction);
}
