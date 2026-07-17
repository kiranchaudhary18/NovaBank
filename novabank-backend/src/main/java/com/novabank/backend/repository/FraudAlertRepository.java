package com.novabank.backend.repository;

import com.novabank.backend.entity.FraudAlert;
import com.novabank.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link FraudAlert} entity.
 * Extends {@link JpaSpecificationExecutor} to enable criteria-based paginated search queries.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, UUID>, JpaSpecificationExecutor<FraudAlert> {

    /**
     * Lists all alerts triggered for a specific user profile.
     *
     * @param user target recipient user
     * @return list of fraud alerts
     */
    List<FraudAlert> findByUser(User user);
}
