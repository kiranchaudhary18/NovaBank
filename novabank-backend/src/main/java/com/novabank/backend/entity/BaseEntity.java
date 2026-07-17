package com.novabank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base abstract class for database entities.
 * Implements ID mapping using UUIDs and timestamps auditing using JPA Auditing.
 *
 * @author Senior Java Backend Architect
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the entity.
     * Uses UUID to mitigate ID enumeration attacks.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * The auditor who created this entity.
     * Managed automatically by JpaAuditingConfig.
     */
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    /**
     * The auditor who last updated this entity.
     * Managed automatically by JpaAuditingConfig.
     */
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    /**
     * Timestamp indicating when this entity was created.
     * Managed automatically by Spring Data JPA Auditing.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating when this entity was last updated.
     * Managed automatically by Spring Data JPA Auditing.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
