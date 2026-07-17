package com.novabank.backend.enums;

/**
 * Represents states during verification of Know Your Customer (KYC) documents.
 *
 * @author Senior Java Backend Architect
 */
public enum KycStatus {
    /** Verification documents have been uploaded and are under review. */
    PENDING,

    /** KYC documents have been reviewed and approved by an employee. */
    APPROVED,

    /** Verification was rejected due to blurry documents, mismatch, etc. */
    REJECTED
}
