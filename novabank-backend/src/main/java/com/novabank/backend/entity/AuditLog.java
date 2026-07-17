package com.novabank.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing security audit log logs.
 * Extends {@link BaseEntity} to inherit UUID key and audit tracking fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_module", columnList = "module"),
                @Index(name = "idx_audit_created", columnList = "created_at"),
                @Index(name = "idx_audit_ip", columnList = "ip_address")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "module", nullable = false, length = 100)
    private String module;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "api", length = 255)
    private String api;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "device", length = 100)
    private String device;

    @Column(name = "browser", length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "description", length = 1000)
    private String description;
}
