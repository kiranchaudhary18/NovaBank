package com.novabank.backend.scheduler;

import com.novabank.backend.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled component executing card expiration validations.
 *
 * @author Senior Java Backend Architect
 */
@Component
@ConditionalOnProperty(name = "novabank.scheduler.cards.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class CardExpiryScheduler {

    private final SchedulerService schedulerService;

    /**
     * Executes cards status expiration validation logs.
     * Default cron: 4:00 AM daily.
     */
    @Scheduled(cron = "${novabank.scheduler.cards.cron:0 0 4 * * ?}")
    public void runCardExpiryCheck() {
        log.info("Card Expiry Scheduler triggered");
        schedulerService.executeJobLifecycle("CARD_EXPIRY_CHECK");
    }
}
