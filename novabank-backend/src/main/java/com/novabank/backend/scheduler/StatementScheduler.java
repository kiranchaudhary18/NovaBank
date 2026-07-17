package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component executing monthly statement calculations.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.statement.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class StatementScheduler {

    private final SchedulerService schedulerService;

    /**
     * Executes the monthly statements compiler batch.
     * Default cron: 2:00 AM on the 1st of every month.
     */
    @Scheduled(cron = "${novabank.scheduler.statement.cron:0 0 2 1 * ?}")
    public void runStatementGeneration() {
        log.info("Monthly Statement Scheduler triggered");
        schedulerService.executeJobLifecycle("STATEMENT_GENERATION");
    }
}
