package com.novabank.backend.repository;

import com.novabank.backend.entity.Role;
import com.novabank.backend.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data Repository for performing queries on the {@link Role} entity.
 *
 * @author Senior Java Backend Architect
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Finds a role by its unique {@link RoleType} enum key.
     *
     * @param roleName the type of role to retrieve
     * @return an optional container containing the found Role entity
     */
    Optional<Role> findByRoleName(RoleType roleName);
}
