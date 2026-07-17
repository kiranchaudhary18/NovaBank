package com.novabank.backend.dto;

import com.novabank.backend.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing customer KYC verification statuses in API responses.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycResponse {

    private UUID id;
    private String aadhaarNumber;
    private String panNumber;
    private String passportNumber;
    private String drivingLicenseNumber;
    private String aadhaarDocument;
    private String panDocument;
    private String passportDocument;
    private KycStatus verificationStatus;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
}
