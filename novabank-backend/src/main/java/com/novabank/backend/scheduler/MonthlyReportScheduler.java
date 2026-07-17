package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component executing monthly aggregate reports.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.monthly.reports.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class MonthlyReportScheduler {

    private final SchedulerService schedulerService;

    /**
     * Compiles monthly system aggregates reports.
     * Default cron: 11:00 PM on the 1st of every month.
     */
    @Scheduled(cron = "${novabank.scheduler.monthly.reports.cron:0 0 23 1 * ?}")
    public void runMonthlyReport() {
        log.info("Monthly Business Report Scheduler triggered");
        schedulerService.executeJobLifecycle("MONTHLY_REPORTS");
    }
}
