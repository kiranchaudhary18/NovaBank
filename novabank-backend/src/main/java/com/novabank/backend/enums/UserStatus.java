package com.novabank.backend.enums;

/**
 * Tracks the general status of a user's login profile credentials.
 * Defines the states: ACTIVE, INACTIVE, BLOCKED, DELETED.
 *
 * @author Senior Java Backend Architect
 */
public enum UserStatus {
    /** User can log in and perform operations normally. */
    ACTIVE,

    /** User account is inactive (e.g. pending email verification). */
    INACTIVE,

    /** User account is blocked due to administrative action or security risks. */
    BLOCKED,

    /** User account is soft-deleted. User can no longer log in. */
    DELETED
}
