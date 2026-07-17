package com.novabank.backend.entity;

import com.novabank.backend.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing user access roles within the banking platform.
 * Extends {@link BaseEntity} to inherit UUID key and audit properties.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Unique name of the role (e.g. ROLE_CUSTOMER, ROLE_ADMIN, ROLE_TELLER, ROLE_MANAGER).
     * Stored as a string enum value.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private RoleType roleName;
}
