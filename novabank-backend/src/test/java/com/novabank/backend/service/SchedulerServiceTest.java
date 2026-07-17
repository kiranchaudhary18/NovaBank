package com.novabank.backend.service;

import com.novabank.backend.dto.JobStatusResponse;
import com.novabank.backend.entity.Account;
import com.novabank.backend.entity.Card;
import com.novabank.backend.entity.JobExecutionLog;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.AccountType;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.repository.AccountRepository;
import com.novabank.backend.repository.CardRepository;
import com.novabank.backend.repository.JobExecutionLogRepository;
import com.novabank.backend.repository.TransactionRepository;
import com.novabank.backend.service.impl.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Layer Unit tests for Phase 14 background schedulers and batch engines.
 * Uses Mockito to mock repository interactions.
 *
 * @author Senior Java Backend Architect
 */
@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Mock
    private JobExecutionLogRepository jobExecutionLogRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private InterestCalculationServiceImpl interestCalculationService;

    @InjectMocks
    private SchedulerServiceImpl schedulerService;

    private Account activeSavingsAccount;
    private Card activeCard;

    @BeforeEach
    void setUp() {
        activeSavingsAccount = Account.builder()
                .accountNumber("NB9990001")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(new BigDecimal("10000.00")) // $10,000 balance
                .build();

        activeCard = Card.builder()
                .cardNumber("4111222233334444")
                .expiryDate(LocalDate.now().minusDays(1)) // Expired yesterday
                .status(CardStatus.ACTIVE)
                .build();
    }

    @Test
    void calculateDailyInterest_Success() {
        Page<Account> page = new PageImpl<>(List.of(activeSavingsAccount));
        Mockito.when(accountRepository.findByAccountTypeAndStatus(
                Mockito.eq(AccountType.SAVINGS), Mockito.eq(AccountStatus.ACTIVE), Mockito.any(Pageable.class)
        )).thenReturn(page);

        int processed = interestCalculationService.calculateDailyInterest();

        // 1 active savings account calculated interest
        Assertions.assertEquals(1, processed);
        // Interest rate of 4% per annum applied daily: 10000 * 0.04 / 365 = ~1.0959. Balance should increase.
        Assertions.assertTrue(activeSavingsAccount.getBalance().compareTo(new BigDecimal("10000.00")) > 0);
        Mockito.verify(accountRepository).save(activeSavingsAccount);
        Mockito.verify(transactionRepository).save(Mockito.any());
    }

    @Test
    void processExpiredCards_Success() {
        Page<Card> page = new PageImpl<>(List.of(activeCard));
        Mockito.when(cardRepository.findByExpiryDateBeforeAndStatus(
                Mockito.any(LocalDate.class), Mockito.eq(CardStatus.ACTIVE), Mockito.any(Pageable.class)
        )).thenReturn(page);

        long processed = schedulerService.processExpiredCards();

        Assertions.assertEquals(1, processed);
        Assertions.assertEquals(CardStatus.EXPIRED, activeCard.getStatus());
        Mockito.verify(cardRepository).save(activeCard);
    }

    @Test
    void executeJobLifecycle_InterestCalculation_Success() {
        Page<Account> page = new PageImpl<>(Collections.emptyList());
        Mockito.lenient().when(accountRepository.findByAccountTypeAndStatus(
                Mockito.eq(AccountType.SAVINGS), Mockito.eq(AccountStatus.ACTIVE), Mockito.any(Pageable.class)
        )).thenReturn(page);

        // Stub save logs
        JobExecutionLog logEntry = JobExecutionLog.builder().jobName("INTEREST_CALCULATION").build();
        Mockito.lenient().when(jobExecutionLogRepository.save(Mockito.any(JobExecutionLog.class))).thenReturn(logEntry);

        // Mock job log lookup used in status check
        Mockito.lenient().when(jobExecutionLogRepository.findAll(Mockito.any(org.springframework.data.jpa.domain.Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        schedulerService.executeJobLifecycle("INTEREST_CALCULATION");

        Mockito.verify(jobExecutionLogRepository, Mockito.times(2)).save(Mockito.any(JobExecutionLog.class));
    }

    @Test
    void getAllJobs_Success() {
        Mockito.lenient().when(jobExecutionLogRepository.findAll(Mockito.any(org.springframework.data.jpa.domain.Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        List<JobStatusResponse> jobs = schedulerService.getAllJobs();
        Assertions.assertFalse(jobs.isEmpty());
        Assertions.assertTrue(jobs.stream().anyMatch(j -> "INTEREST_CALCULATION".equals(j.getJobName())));
    }
}
