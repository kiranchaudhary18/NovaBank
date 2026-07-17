package com.novabank.backend.controller;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.AddressType;
import com.novabank.backend.enums.KycStatus;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.AddressService;
import com.novabank.backend.service.CustomerService;
import com.novabank.backend.service.KycService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Controller exposing customer profile management, address configurations,
 * document uploads, and KYC verification API endpoints.
 * Path mapping: "/api/v1/customers". Protected by stateless JWT authorizations.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management & KYC Module", description = "APIs for customer profiles, addresses, and KYC validations")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;
    private final AddressService addressService;
    private final KycService kycService;

    /**
     * Endpoint to create a customer profile.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Create customer profile", description = "Creates a Customer Profile associated with the authenticated User. Accessible by CUSTOMER role only.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Customer profile created successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "A profile already exists for this account, or input validations failed.")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> createProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CustomerRequest request
    ) {
        log.info("Request to create customer profile for user: {}", user.getEmail());
        CustomerResponse response = customerService.createProfile(user, request);
        return ResponseUtil.created("Customer profile created successfully.", response);
    }

    /**
     * Endpoint to update a customer profile.
     */
    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Update customer profile", description = "Updates profile details and address list for the current authenticated customer. Accessible by CUSTOMER role only.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer profile updated successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Unique constraints check failed (duplicate phone/email) or validation errors.")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CustomerRequest request
    ) {
        log.info("Request to update customer profile for user: {}", user.getEmail());
        CustomerResponse response = customerService.updateProfile(user, request);
        return ResponseUtil.success("Customer profile updated successfully.", response);
    }

    /**
     * Endpoint to get current user customer profile.
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Get current caller customer profile", description = "Fetches the customer profile details of the currently authenticated user. Accessible by CUSTOMER role only.")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyProfile(@AuthenticationPrincipal User user) {
        log.info("Request to fetch customer profile for user: {}", user.getEmail());
        CustomerResponse response = customerService.getMyProfile(user);
        return ResponseUtil.success("Profile retrieved successfully.", response);
    }

    /**
     * Endpoint to get customer profile by UUID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE') or (hasAuthority('ROLE_CUSTOMER') and @customerServiceImpl.getCustomerById(#id).email == authentication.name)")
    @Operation(summary = "Get customer profile by ID", description = "Retrieves customer profile details by ID. Accessible by ADMIN, EMPLOYEE, or the profile owner customer.")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable UUID id) {
        log.info("Request to fetch customer profile for ID: {}", id);
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseUtil.success("Customer profile retrieved successfully.", response);
    }

    /**
     * Endpoint to upload a profile photo image.
     */
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Upload profile photo", description = "Uploads a profile picture for the current user customer. File size limit is 5MB. Allowed formats: JPG, JPEG, PNG.")
    public ResponseEntity<ApiResponse<CustomerResponse>> uploadProfilePhoto(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file
    ) {
        log.info("Request to upload profile photo for user: {}", user.getEmail());
        CustomerResponse response = customerService.uploadProfilePhoto(user, file);
        return ResponseUtil.success("Profile photo uploaded successfully.", response);
    }

    /**
     * Endpoint to submit KYC text details.
     */
    @PostMapping("/kyc")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Submit KYC numbers details", description = "Submits identification card numbers (Aadhaar, PAN, Passport, DL). Validates Aadhaar (12 digits) and PAN (10 chars). Accessible by CUSTOMER role only.")
    public ResponseEntity<ApiResponse<KycResponse>> submitKycDetails(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody KycRequest request
    ) {
        log.info("Request to submit KYC details for user: {}", user.getEmail());
        KycResponse response = kycService.submitKycDetails(user, request);
        return ResponseUtil.success("KYC details submitted successfully.", response);
    }

    /**
     * Endpoint to upload scanned KYC verification document files.
     */
    @PostMapping(value = "/kyc/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Upload KYC document scans", description = "Uploads Aadhaar, PAN, and Passport scan files. Limits to 5MB per file. Formats: JPG, PNG, PDF. Accessible by CUSTOMER role only.")
    public ResponseEntity<ApiResponse<KycResponse>> uploadKycDocuments(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "aadhaarFile", required = false) MultipartFile aadhaarFile,
            @RequestParam(value = "panFile", required = false) MultipartFile panFile,
            @RequestParam(value = "passportFile", required = false) MultipartFile passportFile
    ) {
        log.info("Request to upload KYC documents for user: {}", user.getEmail());
        KycResponse response = kycService.uploadKycDocuments(user, aadhaarFile, panFile, passportFile);
        return ResponseUtil.success("KYC documents uploaded successfully.", response);
    }

    /**
     * Endpoint to retrieve KYC verification details of the current caller.
     */
    @GetMapping("/kyc")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Get current caller KYC details", description = "Fetches the KYC verification status, numbers, and document paths of the authenticated customer.")
    public ResponseEntity<ApiResponse<KycResponse>> getKycDetails(@AuthenticationPrincipal User user) {
        log.info("Request to fetch KYC details for user: {}", user.getEmail());
        KycResponse response = kycService.getKycDetails(user);
        return ResponseUtil.success("KYC details retrieved successfully.", response);
    }

    /**
     * Endpoint to retrieve KYC verification details of a customer by ID.
     */
    @GetMapping("/{id}/kyc")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Get customer KYC details by Customer ID", description = "Retrieves customer KYC details by Customer ID. Accessible by ADMIN and EMPLOYEE roles only.")
    public ResponseEntity<ApiResponse<KycResponse>> getKycDetailsByCustomerId(@PathVariable UUID id) {
        log.info("Request to fetch KYC details for customer ID: {}", id);
        KycResponse response = kycService.getKycDetailsByCustomerId(id);
        return ResponseUtil.success("KYC details retrieved successfully.", response);
    }

    /**
     * Endpoint to verify (approve or reject) customer KYC details.
     */
    @PatchMapping("/{id}/kyc/verify")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'ROLE_ADMIN')")
    @Operation(summary = "Verify customer KYC status", description = "Approves or Rejects a customer's KYC submission. If approved, activates the customer profile status automatically. Accessible by EMPLOYEE and ADMIN only.")
    public ResponseEntity<ApiResponse<KycResponse>> verifyKyc(
            @PathVariable UUID id,
            @AuthenticationPrincipal User verifier,
            @RequestParam KycStatus status
    ) {
        log.info("Request to verify KYC for customer ID: {} with status: {}", id, status);
        KycResponse response = kycService.verifyKyc(id, verifier.getEmail(), status);
        return ResponseUtil.success("KYC verification updated successfully.", response);
    }

    /**
     * Endpoint to create or update customer address details.
     */
    @PutMapping("/address")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Add or update customer address", description = "Creates or updates an address location (RESIDENTIAL, PERMANENT, OFFICE) for the current customer. Accessible by CUSTOMER role only.")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddressRequest request
    ) {
        log.info("Request to update address for user: {}", user.getEmail());
        AddressResponse response = addressService.updateAddress(user, request);
        return ResponseUtil.success("Address details updated successfully.", response);
    }

    /**
     * Endpoint to get customer address details by category type.
     */
    @GetMapping("/address/{type}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Get customer address by type", description = "Retrieves customer address details of the specified type (e.g. RESIDENTIAL). Accessible by CUSTOMER role only.")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddress(
            @AuthenticationPrincipal User user,
            @PathVariable AddressType type
    ) {
        log.info("Request to fetch address of type {} for user: {}", type, user.getEmail());
        AddressResponse response = addressService.getAddress(user, type);
        return ResponseUtil.success("Address details retrieved successfully.", response);
    }

    /**
     * Endpoint to search customer profiles by ID, Phone, Email, PAN, or Aadhaar.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Search customer profiles (Auditors)", description = "Searches customer profiles by customerId, phone, email, pan, or aadhaar. Accessible by ADMIN and EMPLOYEE roles only.")
    public ResponseEntity<ApiResponse<CustomerResponse>> searchCustomer(
            @RequestParam String field,
            @RequestParam String query
    ) {
        log.info("Request to search customer by field: {}, query: {}", field, query);
        CustomerResponse response = customerService.searchCustomer(field, query);
        return ResponseUtil.success("Customer search completed successfully.", response);
    }
}
