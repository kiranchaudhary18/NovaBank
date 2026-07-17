package com.novabank.backend.entity;

import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing bank account records.
 * Extends {@link BaseEntity} to inherit UUID key and audit tracking fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Globally unique sequential account number (e.g. NB100000001). */
    @Column(name = "account_number", unique = true, nullable = false, length = 30)
    private String accountNumber;

    /** Associated customer profile owner. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /** Category type (e.g. SAVINGS, CURRENT). */
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 30)
    private AccountType accountType;

    /** Current total balance. */
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    /** Balance available for withdrawals and holds. */
    @Column(name = "available_balance", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "branch_code", nullable = false, length = 20)
    private String branchCode;

    @Column(name = "ifsc_code", nullable = false, length = 20)
    private String ifscCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private AccountStatus status = AccountStatus.PENDING;

    @Column(name = "opened_date", nullable = false)
    private LocalDate openedDate;

    @Column(name = "closed_date")
    private LocalDate closedDate;

    /** Flag indicating if this is the customer's primary savings account. */
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean isPrimary = false;

    /** Version tracking parameter for JPA Optimistic Locking concurrency controls. */
    @Version
    @Column(name = "version")
    private Long version;
}
