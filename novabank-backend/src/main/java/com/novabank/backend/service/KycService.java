package com.novabank.backend.service;

import com.novabank.backend.dto.KycRequest;
import com.novabank.backend.dto.KycResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.KycStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service interface defining Know Your Customer (KYC) compliance operations.
 *
 * @author Senior Java Backend Architect
 */
public interface KycService {

    /**
     * Submits identifying text numbers (Aadhaar, PAN, Passport, DL) for a customer profile.
     * Enforces database uniqueness on Aadhaar and PAN numbers.
     *
     * @param user current authenticated user
     * @param request identification numbers request
     * @return updated KycResponse details DTO
     */
    KycResponse submitKycDetails(User user, KycRequest request);

    /**
     * Uploads and registers scanned identity verification document files.
     * Sets verification status to PENDING upon submission.
     *
     * @param user current authenticated user
     * @param aadhaarFile Aadhaar card scan file (optional)
     * @param panFile PAN card scan file (optional)
     * @param passportFile Passport scan file (optional)
     * @return updated KycResponse details DTO
     */
    KycResponse uploadKycDocuments(User user, MultipartFile aadhaarFile, MultipartFile panFile, MultipartFile passportFile);

    /**
     * Retrieves the KYC verification details of the current customer user.
     *
     * @param user current authenticated user
     * @return KycResponse details DTO
     */
    KycResponse getKycDetails(User user);

    /**
     * Retrieves the KYC verification details of a customer by their ID (for Admin/Employee audits).
     *
     * @param customerId customer UUID
     * @return KycResponse details DTO
     */
    KycResponse getKycDetailsByCustomerId(UUID customerId);

    /**
     * Verifies (approves or rejects) a customer's KYC submission.
     * Upon approval (APPROVED), the customer's profile status is automatically updated to ACTIVE.
     *
     * @param customerId customer UUID being verified
     * @param verifierEmail email of the bank employee auditor
     * @param status approved or rejected status
     * @return updated KycResponse details DTO
     */
    KycResponse verifyKyc(UUID customerId, String verifierEmail, KycStatus status);
}
