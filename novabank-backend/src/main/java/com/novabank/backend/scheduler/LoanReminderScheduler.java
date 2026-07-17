package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component executing loan EMI payments warning reminders.
 * Stubbed implementation conforming to exclude constraints.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.loans.reminder.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class LoanReminderScheduler {

    private final SchedulerService schedulerService;

    /**
     * Sends payment warnings to clients before loan installment dates.
     * Default cron: 8:00 AM daily.
     */
    @Scheduled(cron = "${novabank.scheduler.loans.reminder.cron:0 0 8 * * ?}")
    public void runLoanReminders() {
        log.info("Loan Installment Reminder Scheduler triggered (Mock Sweep)");
        // Triggers database cleanup or simply executes mock lifecycle
        schedulerService.executeJobLifecycle("MONTHLY_REPORTS");
    }
}
