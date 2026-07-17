package com.novabank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing logs of background batch jobs execution.
 * Extends {@link BaseEntity} to inherit UUID key and audit fields.
 *
 * @author Senior Java Backend Architect
 */
@Entity
@Table(
        name = "job_execution_logs",
        indexes = {
                @Index(name = "idx_job_name", columnList = "job_name"),
                @Index(name = "idx_job_status", columnList = "status"),
                @Index(name = "idx_job_start", columnList = "start_time")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExecutionLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "records_processed")
    @Builder.Default
    private long recordsProcessed = 0L;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;
}
