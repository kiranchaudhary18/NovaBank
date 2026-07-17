package com.novabank.backend.service;

import com.novabank.backend.entity.Card;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.KycStatus;

import java.math.BigDecimal;

/**
 * Service interface executing application event publications.
 *
 * @author Senior Java Backend Architect
 */
public interface EventPublisherService {

    /**
     * Publishes a user registered event.
     *
     * @param user registered user profile
     */
    void publishUserRegisteredEvent(User user);

    /**
     * Publishes a completed bank transaction event.
     *
     * @param transaction completed transaction entity
     */
    void publishTransactionCompletedEvent(Transaction transaction);

    /**
     * Publishes a card issued event.
     *
     * @param card issued card entity
     */
    void publishCardIssuedEvent(Card card);

    /**
     * Publishes a loan approval event.
     *
     * @param user customer recipient
     * @param amount approved loan amount
     */
    void publishLoanApprovedEvent(User user, BigDecimal amount);

    /**
     * Publishes a security credential changed event.
     *
     * @param user password updated user profile
     */
    void publishPasswordChangedEvent(User user);

    /**
     * Publishes a KYC status reviewed event.
     *
     * @param user KYC status updated user
     * @param status approved / rejected state
     */
    void publishKycVerifiedEvent(User user, KycStatus status);
}
