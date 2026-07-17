package com.novabank.backend.enums;

/**
 * Tracks the general status lifecycle of a customer profile.
 *
 * @author Senior Java Backend Architect
 */
public enum CustomerStatus {
    /** Customer profile is fully active and verified. */
    ACTIVE,

    /** Profile is inactive, pending KYC verification or registration finalization. */
    INACTIVE,

    /** Suspended due to fraud checks or bank administrative actions. */
    SUSPENDED,

    /** Profile is soft deleted. */
    DELETED
}
