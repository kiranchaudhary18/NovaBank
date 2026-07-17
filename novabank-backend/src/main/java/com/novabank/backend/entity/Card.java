package com.novabank.backend.entity;

import com.novabank.backend.enums.CardNetwork;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.enums.CardType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing Debit Cards (Physical & Virtual) linked to Bank Accounts.
 * Extends {@link BaseEntity} to inherit UUID key and audit tracking fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(
        name = "cards",
        indexes = {
                @Index(name = "idx_card_num", columnList = "card_number"),
                @Index(name = "idx_card_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "card_number", unique = true, nullable = false, length = 16)
    private String cardNumber;

    @Column(name = "masked_card_number", nullable = false, length = 20)
    private String maskedCardNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 30)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_network", nullable = false, length = 30)
    private CardNetwork cardNetwork;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    /** 3-digit CVV number stored securely. */
    @Column(name = "cvv", nullable = false, length = 10)
    private String cvv;

    @Column(name = "encrypted_pin", nullable = false, length = 255)
    private String encryptedPin;

    @Column(name = "daily_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal dailyLimit;

    @Column(name = "online_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal onlineLimit;

    @Column(name = "atm_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal atmLimit;

    @Column(name = "contactless_enabled", nullable = false)
    @Builder.Default
    private boolean contactlessEnabled = false;

    @Column(name = "international_enabled", nullable = false)
    @Builder.Default
    private boolean internationalEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private CardStatus status = CardStatus.PENDING;

    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    @Column(name = "activated_date")
    private LocalDate activatedDate;

    @Column(name = "blocked_reason", length = 255)
    private String blockedReason;

    @Column(name = "replacement_count", nullable = false)
    @Builder.Default
    private int replacementCount = 0;
}
