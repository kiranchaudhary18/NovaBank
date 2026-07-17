package com.novabank.backend.service;

import com.novabank.backend.dto.AuditLogResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.User;

import java.util.UUID;

/**
 * Service interface executing administrative and operations audit logging.
 *
 * @author Senior Java Backend Architect
 */
public interface AuditService {

    /**
     * Records a business or authentication event to the audit logs.
     *
     * @param user profile executing the action
     * @param action string action key
     * @param module functional module identifier
     * @param status SUCCESS or FAILED
     * @param description audit descriptive details
     * @param httpMethod HTTP request method (optional)
     * @param api requested route path (optional)
     * @param ipAddress caller IP address (optional)
     * @param device user device agent (optional)
     * @param browser user browser engine (optional)
     * @param os user operating system (optional)
     */
    void logAction(
            User user, String action, String module, String status, String description,
            String httpMethod, String api, String ipAddress, String device, String browser, String os
    );

    /**
     * Performs criteria-based paginated search queries over audit log entries.
     *
     * @param page zero-indexed page number
     * @param size page capacity limit
     * @param sortBy sort column key
     * @param sortDir sort direction (asc/desc)
     * @param action filter action (optional)
     * @param module filter module (optional)
     * @param status SUCCESS or FAILED filter (optional)
     * @param userId target user ID (optional)
     * @param ipAddress filter IP address (optional)
     * @return PagedResponse containing AuditLogResponse DTOs
     */
    PagedResponse<AuditLogResponse> searchAuditLogs(
            int page, int size, String sortBy, String sortDir,
            String action, String module, String status, UUID userId, String ipAddress
    );

    /**
     * Fetches detailed audit log values by ID.
     *
     * @param id log UUID
     * @return AuditLogResponse details
     */
    AuditLogResponse getAuditLogById(UUID id);
}
