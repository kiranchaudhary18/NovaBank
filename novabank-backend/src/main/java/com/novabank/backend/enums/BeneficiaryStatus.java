package com.novabank.backend.enums;

/**
 * Tracks the general status lifecycle of a Saved Beneficiary.
 *
 * @author Senior Java Backend Architect
 */
public enum BeneficiaryStatus {
    /** Beneficiary is active and eligible to receive money transfers. */
    ACTIVE,

    /** Newly added beneficiary pending compliance or cooling-off period. */
    PENDING,

    /** Blocked beneficiary. Transfers are disabled. */
    BLOCKED,

    /** Soft-deleted beneficiary. */
    DELETED
}
