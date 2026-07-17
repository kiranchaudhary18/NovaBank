package com.novabank.backend.enums;

/**
 * Tracks the categories of security warnings or fraud triggers.
 *
 * @author Senior Java Backend Architect
 */
public enum AlertType {
    FAILED_LOGIN,
    MULTIPLE_LOGIN,
    LARGE_TRANSACTION,
    HIGH_RISK_TRANSFER,
    MULTIPLE_TRANSFERS,
    SUSPICIOUS_BENEFICIARY,
    ACCOUNT_LOCK,
    PASSWORD_ATTACK
}
