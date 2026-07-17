package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component scanning for past-due credit balances and applying penalty rules.
 * Stubbed implementation conforming to exclude constraints.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.loans.overdue.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class LoanOverdueScheduler {

    private final SchedulerService schedulerService;

    /**
     * Flags overdue installments and updates penance charges.
     * Default cron: 9:00 AM daily.
     */
    @Scheduled(cron = "${novabank.scheduler.loans.overdue.cron:0 0 9 * * ?}")
    public void runLoanOverdueCheck() {
        log.info("Loan Overdue Sweep Scheduler triggered (Mock Sweep)");
        schedulerService.executeJobLifecycle("MONTHLY_REPORTS");
    }
}
