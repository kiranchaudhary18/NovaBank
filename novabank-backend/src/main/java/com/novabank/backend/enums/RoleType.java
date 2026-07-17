package com.novabank.backend.enums;

/**
 * Defines the user security and access roles in the platform.
 *
 * @author Senior Java Backend Architect
 */
public enum RoleType {
    /** General customer utilizing banking features. */
    ROLE_CUSTOMER,

    /** Bank teller handling basic in-branch operations. */
    ROLE_TELLER,

    /** Branch or system manager overseeing customer data and audits. */
    ROLE_MANAGER,

    /** General employee role. */
    ROLE_EMPLOYEE,

    /** Superuser with full system administration privileges. */
    ROLE_ADMIN
}
