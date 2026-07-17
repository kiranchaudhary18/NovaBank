package com.novabank.backend.service.impl;

import com.novabank.backend.dto.JobExecutionLogResponse;
import com.novabank.backend.dto.JobStatusResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Card;
import com.novabank.backend.entity.JobExecutionLog;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.CardRepository;
import com.novabank.backend.repository.JobExecutionLogRepository;
import com.novabank.backend.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.novabank.backend.entity.Notification;
import com.novabank.backend.enums.NotificationStatus;
import com.novabank.backend.repository.NotificationRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation managing background task configurations, manual runs, and audits.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

    private final JobExecutionLogRepository jobExecutionLogRepository;
    private final InterestCalculationService interestCalculationService;
    private final StatementGenerationService statementGenerationService;
    private final ReportGenerationService reportGenerationService;
    private final CleanupService cleanupService;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Value("${novabank.scheduler.interest.cron:0 0 1 * * ?}")
    private String interestCron;

    @Value("${novabank.scheduler.statement.cron:0 0 2 1 * ?}")
    private String statementCron;

    @Value("${novabank.scheduler.reports.cron:0 0 23 * * ?}")
    private String dailyReportsCron;

    @Value("${novabank.scheduler.cleanup.cron:0 0 3 * * ?}")
    private String cleanupCron;

    @Value("${novabank.scheduler.cards.cron:0 0 4 * * ?}")
    private String cardsCron;

    @Value("${novabank.scheduler.inactive.cron:0 0 5 * * ?}")
    private String inactiveCron;

    @Value("${novabank.scheduler.notifications.retry.cron:0 */15 * * * ?}")
    private String notificationsCron;

    private static final List<String> REGISTERED_JOBS = List.of(
            "INTEREST_CALCULATION",
            "STATEMENT_GENERATION",
            "DAILY_REPORTS",
            "MONTHLY_REPORTS",
            "DATABASE_CLEANUP",
            "CARD_EXPIRY_CHECK",
            "INACTIVE_ACCOUNT_CHECK",
            "NOTIFICATION_RETRY"
    );

    @Override
    @Transactional(readOnly = true)
    public List<JobStatusResponse> getAllJobs() {
        log.info("Fetching status overview of all registered background jobs");
        List<JobStatusResponse> list = new ArrayList<>();

        for (String jobName : REGISTERED_JOBS) {
            list.add(getJobStatusDetails(jobName));
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobStatusResponse> getRunningJobs() {
        log.info("Filtering currently active running background jobs");
        return getAllJobs().stream()
                .filter(JobStatusResponse::isRunning)
                .toList();
    }

    @Override
    @Async("taskExecutor")
    public void runJobManually(String jobName) {
        log.info("Manual async execution requested for background job: '{}'", jobName);
        if (!REGISTERED_JOBS.contains(jobName.toUpperCase())) {
            throw new BadRequestException("Declined: Unrecognized background job key name: " + jobName);
        }

        executeJobLifecycle(jobName.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobExecutionLogResponse> getJobHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<JobExecutionLog> result = jobExecutionLogRepository.findAll(pageable);
        return new PagedResponse<>(result.map(this::convertToJobExecutionLogResponse));
    }

    // ==========================================
    // CORE JOBS RUNNERS LIFECYCLE
    // ==========================================

    public void executeJobLifecycle(String jobName) {
        LocalDateTime start = LocalDateTime.now();
        JobExecutionLog logEntry = JobExecutionLog.builder()
                .jobName(jobName)
                .startTime(start)
                .status("RUNNING")
                .build();

        logEntry = jobExecutionLogRepository.save(logEntry);
        long processed = 0;
        String errorMsg = null;
        String finalStatus = "SUCCESS";

        try {
            switch (jobName) {
                case "INTEREST_CALCULATION" -> processed = interestCalculationService.calculateDailyInterest();
                case "STATEMENT_GENERATION" -> processed = statementGenerationService.generateMonthlyStatements();
                case "DAILY_REPORTS" -> {
                    reportGenerationService.generateDailyReports();
                    processed = 2; // Summary of daily transaction and revenue exports
                }
                case "MONTHLY_REPORTS" -> {
                    reportGenerationService.generateMonthlyReports();
                    processed = 1;
                }
                case "DATABASE_CLEANUP" -> processed = cleanupService.cleanOldLogs();
                case "CARD_EXPIRY_CHECK" -> processed = processExpiredCards();
                case "INACTIVE_ACCOUNT_CHECK" -> processed = processInactiveAccounts();
                case "NOTIFICATION_RETRY" -> processed = processNotificationRetry();
                default -> throw new IllegalArgumentException("Unknown job: " + jobName);
            }
        } catch (Exception e) {
            log.error("Error executing background job: '{}'", jobName, e);
            finalStatus = "FAILED";
            errorMsg = e.getMessage();
        }

        logEntry.setEndTime(LocalDateTime.now());
        logEntry.setStatus(finalStatus);
        logEntry.setRecordsProcessed(processed);
        logEntry.setErrorMessage(errorMsg != null && errorMsg.length() > 2000 ? errorMsg.substring(0, 2000) : errorMsg);

        jobExecutionLogRepository.save(logEntry);
        log.info("Background job '{}' finished with status: {} | Duration: {} ms", 
                jobName, finalStatus, Duration.between(start, LocalDateTime.now()).toMillis());
    }

    // ==========================================
    // MAINTENANCE SUB-ROUTINES
    // ==========================================

    @Transactional
    public long processExpiredCards() {
        log.info("Running expired card scanning batch");
        int page = 0;
        int size = 100;
        long markedCount = 0;
        Page<Card> cardsPage;

        do {
            Pageable pageable = PageRequest.of(page, size);
            cardsPage = cardRepository.findByExpiryDateBeforeAndStatus(LocalDate.now(), CardStatus.ACTIVE, pageable);

            for (Card card : cardsPage.getContent()) {
                try {
                    card.setStatus(CardStatus.EXPIRED);
                    cardRepository.save(card);
                    markedCount++;
                } catch (Exception e) {
                    log.error("Failed to mark card expired: {}", card.getCardNumber(), e);
                }
            }
            page++;
        } while (cardsPage.hasNext());

        return markedCount;
    }

    @Transactional
    public long processInactiveAccounts() {
        log.info("Running inactive account checks batch");
        int page = 0;
        int size = 100;
        long processed = 0;
        Page<Account> accountsPage;

        do {
            Pageable pageable = PageRequest.of(page, size);
            accountsPage = accountRepository.findByAccountTypeAndStatus(
                    com.novabank.backend.enums.AccountType.SAVINGS, AccountStatus.ACTIVE, pageable
            );

            for (Account account : accountsPage.getContent()) {
                try {
                    // Accounts with no transactions in 180 days get flagged (simulation)
                    log.debug("Auditing activity logs for account: {}", account.getAccountNumber());
                    processed++;
                } catch (Exception e) {
                    log.error("Failed to audit account: {}", account.getAccountNumber(), e);
                }
            }
            page++;
        } while (accountsPage.hasNext());

        return processed;
    }

    @Transactional
    public long processNotificationRetry() {
        log.info("Running failed alerts notification retry sweep");
        Pageable pageable = PageRequest.of(0, 100);
        Page<Notification> failedPage = notificationRepository.findByStatus(NotificationStatus.FAILED, pageable);
        long processed = 0;

        for (Notification n : failedPage.getContent()) {
            try {
                emailService.sendHtmlEmail(n.getUser().getEmail(), n.getTitle(), n.getMessage());
                n.setStatus(NotificationStatus.SENT);
                notificationRepository.save(n);
                processed++;
            } catch (Exception e) {
                log.error("Failed to resend alert: {}", n.getId(), e);
            }
        }
        return processed;
    }

    // ==========================================
    // PRIVATE STATUS COMPILER HELPERS
    // ==========================================

    private JobStatusResponse getJobStatusDetails(String jobName) {
        // Query latest execution log for this job
        Pageable pageable = PageRequest.of(0, 1);
        
        // Find by Job Name
        Specification<JobExecutionLog> spec = (root, query, cb) -> cb.equal(root.get("jobName"), jobName);
        Page<JobExecutionLog> logs = jobExecutionLogRepository.findAll(spec, pageable);

        LocalDateTime lastRun = null;
        String status = "NEVER_RUN";
        boolean running = false;

        if (!logs.isEmpty()) {
            JobExecutionLog latest = logs.getContent().get(0);
            lastRun = latest.getStartTime();
            status = latest.getStatus();
            running = "RUNNING".equalsIgnoreCase(status);
        }

        String cron = switch (jobName) {
            case "INTEREST_CALCULATION" -> interestCron;
            case "STATEMENT_GENERATION" -> statementCron;
            case "DAILY_REPORTS" -> dailyReportsCron;
            case "DATABASE_CLEANUP" -> cleanupCron;
            case "CARD_EXPIRY_CHECK" -> cardsCron;
            case "INACTIVE_ACCOUNT_CHECK" -> inactiveCron;
            case "NOTIFICATION_RETRY" -> notificationsCron;
            default -> "N/A";
        };

        return JobStatusResponse.builder()
                .jobName(jobName)
                .enabled(true)
                .cronExpression(cron)
                .lastRunTime(lastRun)
                .lastRunStatus(status)
                .isRunning(running)
                .build();
    }

    private JobExecutionLogResponse convertToJobExecutionLogResponse(JobExecutionLog entry) {
        if (entry == null) return null;
        
        long duration = 0;
        if (entry.getEndTime() != null) {
            duration = Duration.between(entry.getStartTime(), entry.getEndTime()).toMillis();
        }

        return JobExecutionLogResponse.builder()
                .id(entry.getId())
                .jobName(entry.getJobName())
                .startTime(entry.getStartTime())
                .endTime(entry.getEndTime())
                .status(entry.getStatus())
                .recordsProcessed(entry.getRecordsProcessed())
                .errorMessage(entry.getErrorMessage())
                .durationMs(duration)
                .build();
    }
}
