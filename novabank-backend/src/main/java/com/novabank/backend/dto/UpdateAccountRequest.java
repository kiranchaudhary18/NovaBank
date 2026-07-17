package com.novabank.backend.dto;

import com.novabank.backend.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing request parameters to update account details.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountRequest {

    /** Optional new operational status (e.g. ACTIVE, FROZEN, BLOCKED, CLOSED). */
    private AccountStatus status;

    /** Optional primary savings account flag override. */
    private Boolean isPrimary;
}
