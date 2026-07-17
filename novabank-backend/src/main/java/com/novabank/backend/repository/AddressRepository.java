package com.novabank.backend.repository;

import com.novabank.backend.entity.Address;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link Address} entity.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    /**
     * Finds a customer's address by category type.
     *
     * @param customer associated Customer
     * @param addressType type of address (e.g. RESIDENTIAL)
     * @return Optional Address entity container
     */
    Optional<Address> findByCustomerAndAddressType(Customer customer, AddressType addressType);
}
