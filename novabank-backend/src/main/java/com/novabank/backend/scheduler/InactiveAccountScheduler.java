package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component executing audits to flag inactive customer accounts.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.inactive.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class InactiveAccountScheduler {

    private final SchedulerService schedulerService;

    /**
     * Executes accounts activity validations.
     * Default cron: 5:00 AM daily.
     */
    @Scheduled(cron = "${novabank.scheduler.inactive.cron:0 0 5 * * ?}")
    public void runInactiveAccountCheck() {
        log.info("Inactive Account Audit Scheduler triggered");
        schedulerService.executeJobLifecycle("INACTIVE_ACCOUNT_CHECK");
    }
}
