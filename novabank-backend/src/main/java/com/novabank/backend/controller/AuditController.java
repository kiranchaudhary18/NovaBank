package com.novabank.backend.controller;

import com.novabank.backend.dto.AuditLogResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.AuditService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller exposing REST API endpoints for querying administrative operations audit log.
 * Path mapping: "/api/v1/admin/audit". Protected by stateless JWT authorizations (restricted to ROLE_ADMIN).
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administrative Security & Audit Logs Module", description = "APIs to query administrative actions history and security audit logs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AuditController {

    private final AuditService auditService;

    /**
     * Endpoint to list and filter audit log entries.
     */
    @GetMapping
    @Operation(summary = "Search and list audit logs (Paginated)", description = "Searches and filters system audit trails. Supported filters: actions, modules, IP address, and status.")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogResponse>>> searchAuditLogs(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String ipAddress
    ) {
        log.info("Administrative audit search query requested by: {}", user.getEmail());
        PagedResponse<AuditLogResponse> response = auditService.searchAuditLogs(
                page, size, sortBy, sortDir, action, module, status, userId, ipAddress
        );
        return ResponseUtil.success("Audit logs list retrieved successfully.", response);
    }

    /**
     * Endpoint to fetch details of a specific audit log by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get audit log details by ID", description = "Retrieves full details of a specific audit log record.")
    public ResponseEntity<ApiResponse<AuditLogResponse>> getAuditLogById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Administrative audit log details requested for ID: {} by: {}", id, user.getEmail());
        AuditLogResponse response = auditService.getAuditLogById(id);
        return ResponseUtil.success("Audit log details retrieved successfully.", response);
    }
}
