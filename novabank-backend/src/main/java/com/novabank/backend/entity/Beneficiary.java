package com.novabank.backend.entity;

import com.novabank.backend.enums.BeneficiaryStatus;
import com.novabank.backend.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing saved money transfer beneficiaries.
 * Extends {@link BaseEntity} to inherit UUID key and audit tracking fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(name = "beneficiaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiary extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** The Customer profile owner who saved this beneficiary. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "beneficiary_name", nullable = false, length = 100)
    private String beneficiaryName;

    @Column(name = "beneficiary_account_number", nullable = false, length = 30)
    private String beneficiaryAccountNumber;

    @Column(name = "beneficiary_bank_name", nullable = false, length = 100)
    private String beneficiaryBankName;

    @Column(name = "beneficiary_ifsc_code", nullable = false, length = 20)
    private String beneficiaryIfscCode;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", nullable = false, length = 30)
    private RelationshipType relationship;

    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private boolean isFavorite = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private BeneficiaryStatus status = BeneficiaryStatus.ACTIVE;
}
