package com.novabank.backend.repository;

import com.novabank.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link User} entity.
 * Extends {@link JpaSpecificationExecutor} to enable criteria-based search queries.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    /**
     * Finds a user profile by their unique login email address.
     *
     * @param email the login email address
     * @return an optional container containing the found User entity
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user profile already exists with the given email address.
     * Used for verifying duplicate emails during registration workflows.
     *
     * @param email the email address to check
     * @return true if a user already exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user already exists with this email address, excluding a specific user ID.
     * Used for verifying email uniqueness during profile updates.
     *
     * @param email the email to verify
     * @param id the user ID to exclude from matching
     * @return true if another user has this email, false otherwise
     */
    boolean existsByEmailAndIdNot(String email, UUID id);

    /**
     * Checks if a user already exists with this phone number, excluding a specific user ID.
     * Used for verifying phone uniqueness during profile updates.
     *
     * @param phone the phone number to verify
     * @param id the user ID to exclude from matching
     * @return true if another user has this phone number, false otherwise
     */
    boolean existsByPhoneAndIdNot(String phone, UUID id);
}
