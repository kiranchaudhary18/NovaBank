package com.novabank.backend.service;

import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;

import java.math.BigDecimal;

/**
 * Service interface executing security monitor triggers.
 * Intercepts platform actions to log audits and evaluate risk rules.
 *
 * @author Senior Java Backend Architect
 */
public interface SecurityMonitoringService {

    /**
     * Records logins audits and evaluates authentication rules.
     *
     * @param user authenticated profile
     * @param success login outcomes (success/failed)
     * @param ip origin IP address
     * @param device user client agent
     * @param browser user browser engine
     * @param os user operating system
     */
    void monitorLogin(User user, boolean success, String ip, String device, String browser, String os);

    /**
     * Records transaction success audits and evaluates transaction risk boundaries.
     *
     * @param transaction completed transaction entity
     */
    void monitorTransaction(Transaction transaction);

    /**
     * Records security credential updates.
     *
     * @param user password updated user profile
     * @param ip origin IP address
     */
    void monitorPasswordChange(User user, String ip);

    /**
     * Records KYC reviews.
     *
     * @param user KYC status updated user
     * @param details KYC check summary (APPROVED/REJECTED)
     */
    void monitorKycUpdate(User user, String details);

    /**
     * Records loan approvals.
     *
     * @param user customer recipient
     * @param amount approved loan amount
     */
    void monitorLoanApproval(User user, BigDecimal amount);

    /**
     * Records card locks/blocks.
     *
     * @param user recipient card owner
     * @param cardDetails card details
     */
    void monitorCardBlock(User user, String cardDetails);
}
