package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component retrying failed alert notifications deliveries.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.notifications.retry.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final SchedulerService schedulerService;

    /**
     * Executes notifications retry checks.
     * Default cron: Every 15 minutes.
     */
    @Scheduled(cron = "${novabank.scheduler.notifications.retry.cron:0 */15 * * * ?}")
    public void runNotificationRetry() {
        log.info("Notification Retry Scheduler triggered");
        schedulerService.executeJobLifecycle("NOTIFICATION_RETRY");
    }
}
