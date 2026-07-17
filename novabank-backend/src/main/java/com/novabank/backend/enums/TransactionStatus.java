package com.novabank.backend.enums;

/**
 * Tracks the general status of a bank transaction.
 *
 * @author Senior Java Backend Architect
 */
public enum TransactionStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    REVERSED
}
