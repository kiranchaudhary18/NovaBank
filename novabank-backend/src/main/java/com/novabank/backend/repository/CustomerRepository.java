package com.novabank.backend.repository;

import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link Customer} entity.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    /**
     * Finds a customer profile by their associated main login User account.
     *
     * @param user associated system user
     * @return Optional Customer entity container
     */
    Optional<Customer> findByUser(User user);

    /**
     * Checks if a Customer profile is already registered for this User account.
     *
     * @param user associated system user
     * @return true if profile exists, false otherwise
     */
    boolean existsByUser(User user);

    /**
     * Finds a customer profile by their auto-generated sequential Customer ID (e.g. CUST000001).
     *
     * @param customerId generated Customer ID string
     * @return Optional Customer entity container
     */
    Optional<Customer> findByCustomerId(String customerId);

    /**
     * Finds a customer profile by their email address.
     *
     * @param email customer email
     * @return Optional Customer entity container
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Finds a customer profile by their phone number.
     *
     * @param phoneNumber customer phone number
     * @return Optional Customer entity container
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Checks if another customer exists with this email address, excluding a specific customer UUID.
     *
     * @param email customer email to check
     * @param id customer UUID to exclude
     * @return true if another customer has this email, false otherwise
     */
    boolean existsByEmailAndIdNot(String email, UUID id);

    /**
     * Checks if another customer exists with this phone number, excluding a specific customer UUID.
     *
     * @param phoneNumber customer phone to check
     * @param id customer UUID to exclude
     * @return true if another customer has this phone number, false otherwise
     */
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID id);

    /**
     * Custom JPQL query to find a Customer profile by associated Aadhaar number.
     *
     * @param aadhaarNumber 12-digit Aadhaar number
     * @return Optional Customer entity container
     */
    @Query("SELECT c FROM Customer c JOIN c.kyc k WHERE k.aadhaarNumber = :aadhaarNumber")
    Optional<Customer> findByAadhaarNumber(@Param("aadhaarNumber") String aadhaarNumber);

    /**
     * Custom JPQL query to find a Customer profile by associated PAN number.
     *
     * @param panNumber 10-character PAN string
     * @return Optional Customer entity container
     */
    @Query("SELECT c FROM Customer c JOIN c.kyc k WHERE k.panNumber = :panNumber")
    Optional<Customer> findByPanNumber(@Param("panNumber") String panNumber);
}
