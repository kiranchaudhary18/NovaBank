package com.novabank.backend.entity;

import com.novabank.backend.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity mapping customer Know Your Customer (KYC) details and verification states.
 * Extends {@link BaseEntity} to inherit UUID key and audit tracking fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(name = "customer_kyc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kyc extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Associated customer profile. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", unique = true, nullable = false)
    private Customer customer;

    /** Unique 12-digit Aadhaar identification number. */
    @Column(name = "aadhaar_number", unique = true, length = 20)
    private String aadhaarNumber;

    /** Unique 10-digit PAN alphanumeric identification key. */
    @Column(name = "pan_number", unique = true, length = 20)
    private String panNumber;

    @Column(name = "passport_number", length = 30)
    private String passportNumber;

    @Column(name = "driving_license_number", length = 30)
    private String drivingLicenseNumber;

    /** File storage path pointing to Aadhaar document scan. */
    @Column(name = "aadhaar_document")
    private String aadhaarDocument;

    /** File storage path pointing to PAN document scan. */
    @Column(name = "pan_document")
    private String panDocument;

    /** File storage path pointing to Passport document scan. */
    @Column(name = "passport_document")
    private String passportDocument;

    /** Current validation review state (e.g. PENDING, APPROVED). */
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    @Builder.Default
    private KycStatus verificationStatus = KycStatus.PENDING;

    /** Email of the employee verifier who reviewed and approved KYC. */
    @Column(name = "verified_by", length = 150)
    private String verifiedBy;

    /** Timestamp of the review approval transaction. */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}
