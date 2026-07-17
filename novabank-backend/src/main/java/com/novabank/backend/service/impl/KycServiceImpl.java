package com.novabank.backend.service.impl;

import com.novabank.backend.dto.KycRequest;
import com.novabank.backend.dto.KycResponse;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.Kyc;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.enums.KycStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.repository.CustomerRepository;
import com.novabank.backend.repository.KycRepository;
import com.novabank.backend.service.KycService;
import com.novabank.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service implementation managing customer {@link Kyc} verification flows.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KycServiceImpl implements KycService {

    private final CustomerRepository customerRepository;
    private final KycRepository kycRepository;
    private final com.novabank.backend.service.EventPublisherService eventPublisherService;

    @Override
    @Transactional
    public KycResponse submitKycDetails(User user, KycRequest request) {
        log.info("Submitting KYC text parameters for user: {}", user.getEmail());
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));

        Kyc kyc = kycRepository.findByCustomer(customer)
                .orElseGet(() -> Kyc.builder()
                        .customer(customer)
                        .verificationStatus(KycStatus.PENDING)
                        .build());

        // Validate duplicates
        if (kyc.getId() == null) {
            if (kycRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
                throw new BadRequestException("Aadhaar card number is already registered under another account.");
            }
            if (kycRepository.existsByPanNumber(request.getPanNumber())) {
                throw new BadRequestException("PAN card number is already registered under another account.");
            }
        } else {
            if (kycRepository.existsByAadhaarNumberAndIdNot(request.getAadhaarNumber(), kyc.getId())) {
                throw new BadRequestException("Aadhaar card number is already registered under another account.");
            }
            if (kycRepository.existsByPanNumberAndIdNot(request.getPanNumber(), kyc.getId())) {
                throw new BadRequestException("PAN card number is already registered under another account.");
            }
        }

        kyc.setAadhaarNumber(request.getAadhaarNumber());
        kyc.setPanNumber(request.getPanNumber());
        kyc.setPassportNumber(request.getPassportNumber());
        kyc.setDrivingLicenseNumber(request.getDrivingLicenseNumber());

        Kyc savedKyc = kycRepository.save(kyc);
        log.info("KYC details registered successfully for ID: {}", savedKyc.getId());
        return convertToKycResponse(savedKyc);
    }

    @Override
    @Transactional
    public KycResponse uploadKycDocuments(User user, MultipartFile aadhaarFile, MultipartFile panFile, MultipartFile passportFile) {
        log.info("Uploading KYC documents for user: {}", user.getEmail());
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));

        Kyc kyc = kycRepository.findByCustomer(customer)
                .orElseGet(() -> Kyc.builder()
                        .customer(customer)
                        .verificationStatus(KycStatus.PENDING)
                        .build());

        // Upload documents if supplied
        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
            kyc.setAadhaarDocument(FileUploadUtil.saveFile("kyc", aadhaarFile));
        }
        if (panFile != null && !panFile.isEmpty()) {
            kyc.setPanDocument(FileUploadUtil.saveFile("kyc", panFile));
        }
        if (passportFile != null && !passportFile.isEmpty()) {
            kyc.setPassportDocument(FileUploadUtil.saveFile("kyc", passportFile));
        }

        // Reset state status to PENDING on document updates
        kyc.setVerificationStatus(KycStatus.PENDING);

        Kyc savedKyc = kycRepository.save(kyc);
        log.info("KYC verification documents registered successfully for ID: {}", savedKyc.getId());
        return convertToKycResponse(savedKyc);
    }

    @Override
    @Transactional(readOnly = true)
    public KycResponse getKycDetails(User user) {
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for this account."));

        Kyc kyc = kycRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("KYC details not found for this account."));

        return convertToKycResponse(kyc);
    }

    @Override
    @Transactional(readOnly = true)
    public KycResponse getKycDetailsByCustomerId(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        Kyc kyc = kycRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("KYC details not found for customer: " + customerId));

        return convertToKycResponse(kyc);
    }

    @Override
    @Transactional
    public KycResponse verifyKyc(UUID customerId, String verifierEmail, KycStatus status) {
        log.info("Auditing KYC status to {} for customer ID: {} by verifier: {}", status, customerId, verifierEmail);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        Kyc kyc = kycRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("KYC details not found for customer ID: " + customerId));

        // Enforce customer account status activation upon approved KYC checks
        if (status == KycStatus.APPROVED) {
            customer.setStatus(CustomerStatus.ACTIVE);
            customerRepository.save(customer);
            log.info("Customer ID {} state status updated to ACTIVE", customerId);
        } else if (status == KycStatus.REJECTED) {
            customer.setStatus(CustomerStatus.INACTIVE);
            customerRepository.save(customer);
        }

        kyc.setVerificationStatus(status);
        kyc.setVerifiedBy(verifierEmail);
        kyc.setVerifiedAt(LocalDateTime.now());

        Kyc savedKyc = kycRepository.save(kyc);
        log.info("KYC verification process successfully updated for customer ID: {}", customerId);

        // Publish event to trigger KYC email & notifications
        eventPublisherService.publishKycVerifiedEvent(customer.getUser(), status);

        return convertToKycResponse(savedKyc);
    }

    private KycResponse convertToKycResponse(Kyc kyc) {
        if (kyc == null) {
            return null;
        }
        return KycResponse.builder()
                .id(kyc.getId())
                .aadhaarNumber(kyc.getAadhaarNumber())
                .panNumber(kyc.getPanNumber())
                .passportNumber(kyc.getPassportNumber())
                .drivingLicenseNumber(kyc.getDrivingLicenseNumber())
                .aadhaarDocument(kyc.getAadhaarDocument())
                .panDocument(kyc.getPanDocument())
                .passportDocument(kyc.getPassportDocument())
                .verificationStatus(kyc.getVerificationStatus())
                .verifiedBy(kyc.getVerifiedBy())
                .verifiedAt(kyc.getVerifiedAt())
                .build();
    }
}
