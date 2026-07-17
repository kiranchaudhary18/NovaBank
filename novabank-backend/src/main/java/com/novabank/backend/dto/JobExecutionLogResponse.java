package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing history logs of completed background executions.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobExecutionLogResponse {

    private UUID id;
    private String jobName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private long recordsProcessed;
    private String errorMessage;
    private long durationMs;
}
