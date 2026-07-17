package com.novabank.backend.enums;

/**
 * Tracks the general status of a bank account.
 *
 * @author Senior Java Backend Architect
 */
public enum AccountStatus {
    /** Account is newly opened and awaiting validation checks. */
    PENDING,

    /** Active status, normal transactions allowed. */
    ACTIVE,

    /** Frozen status, debit/credit transactions suspended. */
    FROZEN,

    /** Blocked status, account locked by security admin. */
    BLOCKED,

    /** Closed status, final settlement done, no longer usable. */
    CLOSED
}
