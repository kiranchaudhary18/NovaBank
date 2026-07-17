package com.novabank.backend.controller;

import com.novabank.backend.dto.JobExecutionLogResponse;
import com.novabank.backend.dto.JobStatusResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.SchedulerService;
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

import java.util.List;

/**
 * Controller exposing REST API endpoints to manage scheduled background batch jobs.
 * Path mapping: "/api/v1/admin/jobs". Protected by stateless JWT authorizations (restricted to ROLE_ADMIN).
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/admin/jobs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administrative Schedulers & Jobs Module", description = "APIs to monitor background schedulers, list job status execution logs, and run jobs manually")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class JobController {

    private final SchedulerService schedulerService;

    /**
     * Endpoint to list status details of all background jobs.
     */
    @GetMapping
    @Operation(summary = "List all registered background jobs status details", description = "Retrieves configurations, crons, status, and running indicators of all schedulers.")
    public ResponseEntity<ApiResponse<List<JobStatusResponse>>> getAllJobs(
            @AuthenticationPrincipal User user
    ) {
        log.info("Administrative fetch of all jobs configurations requested by: {}", user.getEmail());
        List<JobStatusResponse> response = schedulerService.getAllJobs();
        return ResponseUtil.success("Background jobs list compiled successfully.", response);
    }

    /**
     * Endpoint to list past job execution history logs.
     */
    @GetMapping("/history")
    @Operation(summary = "List completed background job execution logs (Paginated)", description = "Searches and paginates execution durations, records processed, and failure messages.")
    public ResponseEntity<ApiResponse<PagedResponse<JobExecutionLogResponse>>> getJobHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Administrative search of jobs history log requested by: {}", user.getEmail());
        PagedResponse<JobExecutionLogResponse> response = schedulerService.getJobHistory(page, size);
        return ResponseUtil.success("Job execution logs retrieved successfully.", response);
    }

    /**
     * Endpoint to list jobs currently running.
     */
    @GetMapping("/status")
    @Operation(summary = "List currently active running scheduled jobs", description = "Retrieves logs for jobs with status 'RUNNING'.")
    public ResponseEntity<ApiResponse<List<JobStatusResponse>>> getRunningJobs(
            @AuthenticationPrincipal User user
    ) {
        log.info("Administrative search of active running jobs requested by: {}", user.getEmail());
        List<JobStatusResponse> response = schedulerService.getRunningJobs();
        return ResponseUtil.success("Running jobs list compiled successfully.", response);
    }

    /**
     * Endpoint to manually trigger a job.
     */
    @PostMapping("/{jobName}/run")
    @Operation(summary = "Trigger manual background job execution (Asynchronous)", description = "Spawns an asynchronous background thread execution for a job identify key name.")
    public ResponseEntity<ApiResponse<Void>> runJobManually(
            @AuthenticationPrincipal User user,
            @PathVariable String jobName
    ) {
        log.info("Administrative manual execution trigger for job '{}' requested by: {}", jobName, user.getEmail());
        schedulerService.runJobManually(jobName);
        return ResponseUtil.success("Manual execution request registered. Job is running in background.", null);
    }
}
