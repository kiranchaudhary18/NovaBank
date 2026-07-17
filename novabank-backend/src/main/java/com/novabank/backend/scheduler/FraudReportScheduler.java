package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component executing daily fraud reports assemblies.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.reports.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class FraudReportScheduler {

    private final SchedulerService schedulerService;

    /**
     * Compiles daily fraud threats reports.
     * Default cron: 11:00 PM daily.
     */
    @Scheduled(cron = "${novabank.scheduler.reports.cron:0 0 23 * * ?}")
    public void runFraudReport() {
        log.info("Daily Fraud and Security Report Scheduler triggered");
        schedulerService.executeJobLifecycle("DAILY_REPORTS");
    }
}
