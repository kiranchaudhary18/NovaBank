package com.novabank.backend.enums;

/**
 * Tracks the security status of issued cards.
 *
 * @author Senior Java Backend Architect
 */
public enum CardStatus {
    PENDING,
    ACTIVE,
    BLOCKED,
    FROZEN,
    EXPIRED,
    REPLACED
}
