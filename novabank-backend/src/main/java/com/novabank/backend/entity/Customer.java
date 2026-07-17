package com.novabank.backend.entity;

import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.enums.Gender;
import com.novabank.backend.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity mapping user profiles to their specific Customer data sheets.
 * Extends {@link BaseEntity} to inherit UUID key and audit tracking fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Associated main system login user account profile. */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    /** Auto-generated sequential Customer ID (e.g. CUST000001). */
    @Column(name = "customer_id", unique = true, nullable = false, length = 30)
    private String customerId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "annual_income", precision = 15, scale = 2)
    private BigDecimal annualIncome;

    @Column(name = "nationality", length = 50)
    private String nationality;

    /** Relative storage path to the uploaded profile photo image. */
    @Column(name = "profile_photo")
    private String profilePhoto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.INACTIVE;

    /** List of physical address locations. */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    /** KYC verification details. */
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Kyc kyc;

    /**
     * Helper method to sync customer relationships for addresses.
     *
     * @param address the address to add
     */
    public void addAddress(Address address) {
        if (address != null) {
            addresses.add(address);
            address.setCustomer(this);
        }
    }
}
