package com.novabank.backend.repository;

import com.novabank.backend.entity.NotificationPreference;
import com.novabank.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link NotificationPreference} entity.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {

    /**
     * Finds notification preference settings configured by a user.
     *
     * @param user target user
     * @return Optional containing notification preference details, or empty
     */
    Optional<NotificationPreference> findByUser(User user);
}
