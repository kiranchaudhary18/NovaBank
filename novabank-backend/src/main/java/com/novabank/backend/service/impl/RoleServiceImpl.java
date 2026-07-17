package com.novabank.backend.service.impl;

import com.novabank.backend.entity.Role;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.RoleRepository;
import com.novabank.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing {@link Role} entities.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByName(RoleType roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
