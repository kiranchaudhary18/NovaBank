package com.novabank.backend.service;

import com.novabank.backend.dto.FraudAlertResponse;
import com.novabank.backend.dto.PagedResponse;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AlertStatus;
import com.novabank.backend.enums.AlertType;
import com.novabank.backend.enums.RiskLevel;

import java.util.UUID;

/**
 * Service interface implementing the fraud detection rules engine.
 * Evaluates core transactions, alerts admins, and updates alert review statuses.
 *
 * @author Senior Java Backend Architect
 */
public interface FraudDetectionService {

    /**
     * Evaluates security rules on a newly completed transaction.
     * Checks for large transfers, frozen account withdrawals, and suspicious beneficiary activities.
     *
     * @param transaction completed transaction log
     */
    void evaluateTransactionRules(Transaction transaction);

    /**
     * Evaluates security rules on authentication attempts (failed logins, multi-device logins).
     *
     * @param user authenticated profile
     * @param isSuccess login success status
     * @param ip origin IP address
     * @param device caller device agent
     * @param browser caller browser
     * @param os caller operating system
     */
    void evaluateAuthenticationRules(User user, boolean isSuccess, String ip, String device, String browser, String os);

    /**
     * Lists, filters, and searches triggered fraud alerts (paginated).
     *
     * @param page zero-indexed page number
     * @param size page capacity limit
     * @param sortBy sort column key
     * @param sortDir sort direction (asc/desc)
     * @param riskLevel filter by RiskLevel (optional)
     * @param alertType filter by AlertType (optional)
     * @param status filter by AlertStatus (optional)
     * @param customerId filter by associated Customer ID (optional)
     * @return PagedResponse containing FraudAlertResponse details
     */
    PagedResponse<FraudAlertResponse> searchFraudAlerts(
            int page, int size, String sortBy, String sortDir,
            RiskLevel riskLevel, AlertType alertType, AlertStatus status, UUID customerId
    );

    /**
     * Fetches detailed information of a fraud alert by ID.
     *
     * @param id alert UUID
     * @return FraudAlertResponse details
     */
    FraudAlertResponse getFraudAlertById(UUID id);

    /**
     * Moves an open fraud alert to UNDER_REVIEW.
     *
     * @param id alert UUID
     * @param reviewer email address of the reviewer employee
     * @return updated FraudAlertResponse details
     */
    FraudAlertResponse reviewFraudAlert(UUID id, String reviewer);

    /**
     * Resolves a fraud alert (marks as RESOLVED or FALSE_POSITIVE).
     *
     * @param id alert UUID
     * @param reviewer email address of the reviewer employee
     * @param resolutionStatus RESOLVED or FALSE_POSITIVE status
     * @return updated FraudAlertResponse details
     */
    FraudAlertResponse resolveFraudAlert(UUID id, String reviewer, AlertStatus resolutionStatus);
}
