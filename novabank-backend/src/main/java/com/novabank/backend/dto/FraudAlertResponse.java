package com.novabank.backend.dto;

import com.novabank.backend.enums.AlertStatus;
import com.novabank.backend.enums.AlertType;
import com.novabank.backend.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing serialized security warnings.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlertResponse {

    private UUID id;
    private UUID userId;
    private String userEmail;
    private UUID accountId;
    private String accountNumber;
    private UUID transactionId;
    private String transactionReference;
    private AlertType alertType;
    private RiskLevel riskLevel;
    private int riskScore;
    private String reason;
    private AlertStatus status;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
