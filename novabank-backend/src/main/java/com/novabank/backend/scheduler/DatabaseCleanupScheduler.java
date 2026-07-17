package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component executing periodic log cleanup.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.cleanup.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleanupScheduler {

    private final SchedulerService schedulerService;

    /**
     * Prunes expired audits logs from Postgres DB.
     * Default cron: 3:00 AM daily.
     */
    @Scheduled(cron = "${novabank.scheduler.cleanup.cron:0 0 3 * * ?}")
    public void runDatabaseCleanup() {
        log.info("Database Pruning Cleanup Scheduler triggered");
        schedulerService.executeJobLifecycle("DATABASE_CLEANUP");
    }
}
