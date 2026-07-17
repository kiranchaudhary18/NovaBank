package com.novabank.backend.service;

import com.novabank.backend.dto.JobExecutionLogResponse;
import com.novabank.backend.dto.JobStatusResponse;
import com.novabank.backend.dto.PagedResponse;

import java.util.List;

/**
 * Service interface managing administrative schedulers controls and history monitoring.
 *
 * @author Senior Java Backend Architect
 */
public interface SchedulerService {

    /**
     * Lists configurations, crons, and running status parameters of all background jobs.
     *
     * @return list of JobStatusResponse DTOs
     */
    List<JobStatusResponse> getAllJobs();

    /**
     * Lists jobs currently running.
     *
     * @return list of running job status responses
     */
    List<JobStatusResponse> getRunningJobs();

    /**
     * Triggers a manual, asynchronous execution of a background task.
     *
     * @param jobName key identify name of the job
     */
    void runJobManually(String jobName);

    /**
     * Returns paginated listings of completed or failed job execution logs.
     *
     * @param page zero-indexed page number
     * @param size page limit capacity
     * @return PagedResponse containing JobExecutionLogResponse DTOs
     */
    PagedResponse<JobExecutionLogResponse> getJobHistory(int page, int size);

    /**
     * Executes the lifecycle logic of a background job synchronously.
     * Used by standard scheduled crons and manual triggers.
     *
     * @param jobName uppercase job name
     */
    void executeJobLifecycle(String jobName);
}
