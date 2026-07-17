package com.novabank.backend.dto;

import com.novabank.backend.enums.RoleType;
import com.novabank.backend.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object encapsulating filter criteria to perform user searches.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterRequest {

    /** Partial or full name criteria. */
    private String name;

    /** Email address criteria. */
    private String email;

    /** Phone number criteria. */
    private String phone;

    /** Current user account state. */
    private UserStatus status;

    /** Assigned access privilege role. */
    private RoleType role;
}
