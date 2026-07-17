package com.novabank.backend.repository;

import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link Kyc} entity.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface KycRepository extends JpaRepository<Kyc, UUID> {

    /**
     * Finds verification details of a specific Customer profile.
     *
     * @param customer associated Customer entity
     * @return Optional Kyc entity container
     */
    Optional<Kyc> findByCustomer(Customer customer);

    /**
     * Checks if Aadhaar number is already used in a verification sheet.
     *
     * @param aadhaarNumber Aadhaar string
     * @return true if Aadhaar already exists, false otherwise
     */
    boolean existsByAadhaarNumber(String aadhaarNumber);

    /**
     * Checks if PAN number is already used in a verification sheet.
     *
     * @param panNumber PAN string
     * @return true if PAN already exists, false otherwise
     */
    boolean existsByPanNumber(String panNumber);

    /**
     * Checks if another verification contains this Aadhaar, excluding a specific Kyc UUID.
     *
     * @param aadhaarNumber Aadhaar string
     * @param id Kyc UUID to exclude
     * @return true if another sheet has this Aadhaar, false otherwise
     */
    boolean existsByAadhaarNumberAndIdNot(String aadhaarNumber, UUID id);

    /**
     * Checks if another verification contains this PAN, excluding a specific Kyc UUID.
     *
     * @param panNumber PAN string
     * @param id Kyc UUID to exclude
     * @return true if another sheet has this PAN, false otherwise
     */
    boolean existsByPanNumberAndIdNot(String panNumber, UUID id);
}
