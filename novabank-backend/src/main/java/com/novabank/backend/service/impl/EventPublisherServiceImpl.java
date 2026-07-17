package com.novabank.backend.service.impl;

import com.novabank.backend.entity.Card;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.KycStatus;
import com.novabank.backend.event.*;
import com.novabank.backend.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service implementation publishing Spring events to application contexts.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishUserRegisteredEvent(User user) {
        log.debug("Publishing UserRegisteredEvent for email: {}", user.getEmail());
        eventPublisher.publishEvent(new UserRegisteredEvent(user));
    }

    @Override
    public void publishTransactionCompletedEvent(Transaction transaction) {
        log.debug("Publishing TransactionCompletedEvent for ID: {}", transaction.getTransactionId());
        eventPublisher.publishEvent(new TransactionCompletedEvent(transaction));
    }

    @Override
    public void publishCardIssuedEvent(Card card) {
        log.debug("Publishing CardIssuedEvent for card: {}", card.getMaskedCardNumber());
        eventPublisher.publishEvent(new CardIssuedEvent(card));
    }

    @Override
    public void publishLoanApprovedEvent(User user, BigDecimal amount) {
        log.debug("Publishing LoanApprovedEvent for user: {} with amount: {}", user.getEmail(), amount);
        eventPublisher.publishEvent(new LoanApprovedEvent(user, amount));
    }

    @Override
    public void publishPasswordChangedEvent(User user) {
        log.debug("Publishing PasswordChangedEvent for email: {}", user.getEmail());
        eventPublisher.publishEvent(new PasswordChangedEvent(user));
    }

    @Override
    public void publishKycVerifiedEvent(User user, KycStatus status) {
        log.debug("Publishing KycVerifiedEvent for user: {} with status: {}", user.getEmail(), status);
        eventPublisher.publishEvent(new KycVerifiedEvent(user, status));
    }
}
