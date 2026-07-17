package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing serialized security audit logs logs.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private UUID id;
    private UUID userId;
    private String userEmail;
    private String action;
    private String module;
    private String httpMethod;
    private String api;
    private String requestId;
    private String ipAddress;
    private String device;
    private String browser;
    private String operatingSystem;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
