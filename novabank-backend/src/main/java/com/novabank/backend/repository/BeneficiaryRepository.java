package com.novabank.backend.repository;

import com.novabank.backend.entity.Beneficiary;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.enums.BeneficiaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link Beneficiary} entity.
 * Extends {@link JpaSpecificationExecutor} to enable criteria-based paginated search queries.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID>, JpaSpecificationExecutor<Beneficiary> {

    /**
     * Checks if a beneficiary account number is already registered for this customer,
     * ignoring beneficiaries that are in DELETED status.
     *
     * @param customer customer profile owner
     * @param accountNumber beneficiary account number to verify
     * @param status status to exclude (typically BeneficiaryStatus.DELETED)
     * @return true if duplicate exists, false otherwise
     */
    boolean existsByCustomerAndBeneficiaryAccountNumberAndStatusNot(Customer customer, String accountNumber, BeneficiaryStatus status);
}
