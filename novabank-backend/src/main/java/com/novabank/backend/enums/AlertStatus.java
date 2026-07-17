package com.novabank.backend.enums;

/**
 * Tracks the review lifecycle status of a fraud warning.
 *
 * @author Senior Java Backend Architect
 */
public enum AlertStatus {
    OPEN,
    UNDER_REVIEW,
    RESOLVED,
    FALSE_POSITIVE
}
