package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component executing daily interest calculation batches on Savings Accounts.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.interest.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class InterestCalculationScheduler {

    private final SchedulerService schedulerService;

    /**
     * Executes the daily interest calculations runner.
     * Default cron: 1:00 AM daily.
     */
    @Scheduled(cron = "${novabank.scheduler.interest.cron:0 0 1 * * ?}")
    public void runInterestCalculation() {
        log.info("Interest Calculation Scheduler triggered");
        schedulerService.executeJobLifecycle("INTEREST_CALCULATION");
    }
}
