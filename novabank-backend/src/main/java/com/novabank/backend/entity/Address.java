package com.novabank.backend.entity;

import com.novabank.backend.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing customer address location mappings.
 * Extends {@link BaseEntity} to inherit UUID key and audit tracking fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Associated customer owner. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /** Type categorization of this address location (e.g. RESIDENTIAL). */
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 30)
    private AddressType addressType;

    @Column(name = "house_number", nullable = false, length = 30)
    private String houseNumber;

    @Column(name = "street", nullable = false, length = 150)
    private String street;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "postal_code", nullable = false, length = 15)
    private String postalCode;
}
