package com.novabank.backend.entity;

import com.novabank.backend.enums.TransactionStatus;
import com.novabank.backend.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing core banking transactions.
 * Extends {@link BaseEntity} to inherit UUID key and audit tracking fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_txn_id", columnList = "transaction_id"),
                @Index(name = "idx_ref_num", columnList = "reference_number"),
                @Index(name = "idx_txn_date", columnList = "transaction_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Globally unique sequential transaction ID (e.g. TXN1712838933). */
    @Column(name = "transaction_id", unique = true, nullable = false, length = 50)
    private String transactionId;

    /** Reference tracking number for receipt correlations. */
    @Column(name = "reference_number", unique = true, nullable = false, length = 50)
    private String referenceNumber;

    /** Associated debiting account (null for external CASH deposits). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_account_id")
    private Account senderAccount;

    /** Associated crediting account (null for external CASH withdrawals). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_account_id")
    private Account receiverAccount;

    /** Optional saved beneficiary receiver metadata. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "beneficiary_id")
    private Beneficiary beneficiary;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "opening_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal openingBalance;

    @Column(name = "closing_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal closingBalance;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "remarks", length = 255)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "initiated_by", nullable = false, length = 150)
    private String initiatedBy;

    @Column(name = "approved_by", length = 150)
    private String approvedBy;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
}
