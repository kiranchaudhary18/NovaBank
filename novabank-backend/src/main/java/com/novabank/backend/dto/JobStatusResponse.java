package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing configuration status and health flags of a scheduled job.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobStatusResponse {

    private String jobName;
    private boolean enabled;
    private String cronExpression;
    private LocalDateTime lastRunTime;
    private String lastRunStatus;
    private boolean isRunning;
}
