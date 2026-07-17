package com.novabank.backend.enums;

/**
 * Tracks the state of a customer's loan from application to pay-off.
 *
 * @author Senior Java Backend Architect
 */
public enum LoanStatus {
    /** Loan application has been submitted and is under review. */
    APPLIED,

    /** Loan application has been approved by the bank. */
    APPROVED,

    /** Loan application was rejected. */
    REJECTED,

    /** Loan has been disbursed and is currently active/outstanding. */
    ACTIVE,

    /** Loan has been fully paid off and closed. */
    PAID_OFF,

    /** Loan is in default (missed payments exceeding threshold). */
    DEFAULTED
}
