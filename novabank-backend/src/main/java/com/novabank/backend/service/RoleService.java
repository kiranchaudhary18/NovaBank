package com.novabank.backend.service;

import com.novabank.backend.entity.Role;
import com.novabank.backend.enums.RoleType;

import java.util.List;

/**
 * Service interface defining operations related to Role management.
 *
 * @author Senior Java Backend Architect
 */
public interface RoleService {

    /**
     * Finds a security Role entity by its name.
     *
     * @param roleName role identifier enum
     * @return the Role entity
     * @throws com.novabank.backend.exception.ResourceNotFoundException if role is not found
     */
    Role getRoleByName(RoleType roleName);

    /**
     * Lists all security roles populated in the database.
     *
     * @return list of Role entities
     */
    List<Role> getAllRoles();
}
