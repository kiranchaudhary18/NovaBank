package com.novabank.backend.controller;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.BeneficiaryStatus;
import com.novabank.backend.enums.RelationshipType;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.BeneficiaryService;
import com.novabank.backend.service.CustomerService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller exposing REST API endpoints for Beneficiary Management.
 * Path mapping: "/api/v1/beneficiaries". Protected by stateless JWT authorizations.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/beneficiaries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Beneficiary Management Module", description = "APIs to manage transfer beneficiaries saved by customers")
@SecurityRequirement(name = "bearerAuth")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;
    private final CustomerService customerService;

    /**
     * Endpoint to add a new beneficiary.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Save a beneficiary", description = "Saves a new money transfer beneficiary. Customers can only save beneficiaries for themselves. Enforces self-account checks and duplication checks.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Beneficiary saved successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Duplicate account number, adding own account, invalid IFSC, or validation failure.")
    })
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> addBeneficiary(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateBeneficiaryRequest request
    ) {
        log.info("Request to save beneficiary by customer: {}", user.getEmail());
        BeneficiaryResponse response = beneficiaryService.addBeneficiary(user, request);
        return ResponseUtil.created("Beneficiary saved successfully.", response);
    }

    /**
     * Endpoint to retrieve details of a saved beneficiary by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get beneficiary details by ID", description = "Retrieves details of a saved beneficiary by ID. Customers can only view their own beneficiaries; Employees/Admins can view any saved template.")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> getBeneficiaryById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Request to fetch beneficiary ID: {} by user: {}", id, user.getEmail());
        BeneficiaryResponse response = beneficiaryService.getBeneficiaryById(id);

        // Security check: Customer can only view their own saved beneficiaries
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            if (!response.getCustomerId().equals(customerProfile.getId())) {
                throw new ForbiddenException("Unauthorized: You do not own this beneficiary.");
            }
        }

        return ResponseUtil.success("Beneficiary details retrieved successfully.", response);
    }

    /**
     * Endpoint to list and filter beneficiaries dynamically.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "List and search beneficiaries (Paginated)", description = "Searches and filters saved transfer beneficiaries. Customers can only view their own; Employees/Admins can query across all.")
    public ResponseEntity<ApiResponse<PagedResponse<BeneficiaryResponse>>> searchBeneficiaries(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) BeneficiaryStatus status,
            @RequestParam(required = false) Boolean isFavorite,
            @RequestParam(required = false) RelationshipType relationship,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) UUID customerId
    ) {
        log.info("Search request for beneficiaries by user: {}", user.getEmail());
        UUID customerIdFilter;

        // Security check: Customer can only list their own beneficiaries
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            customerIdFilter = customerProfile.getId();
        } else {
            customerIdFilter = customerId;
        }

        PagedResponse<BeneficiaryResponse> response = beneficiaryService.searchBeneficiaries(
                page, size, sortBy, sortDir, status, isFavorite, relationship, name, accountNumber, nickname, customerIdFilter
        );
        return ResponseUtil.success("Beneficiaries list retrieved successfully.", response);
    }

    /**
     * Endpoint to update beneficiary details.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Update beneficiary details", description = "Updates details (name, bank name, IFSC, relationship, nickname) of a saved beneficiary. Restricted to the owner customer.")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> updateBeneficiary(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBeneficiaryRequest request
    ) {
        log.info("Update request for beneficiary ID: {} by customer: {}", id, user.getEmail());
        BeneficiaryResponse existing = beneficiaryService.getBeneficiaryById(id);

        // Security check: Only owner customer can edit details
        CustomerResponse customerProfile = customerService.getMyProfile(user);
        if (!existing.getCustomerId().equals(customerProfile.getId())) {
            throw new ForbiddenException("Unauthorized: You do not own this beneficiary.");
        }

        BeneficiaryResponse response = beneficiaryService.updateBeneficiary(id, request);
        return ResponseUtil.success("Beneficiary updated successfully.", response);
    }

    /**
     * Endpoint to toggle favorite flag.
     */
    @PatchMapping("/{id}/favorite")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Toggle favorite beneficiary flag", description = "Toggles the favorite flag of a saved beneficiary. Restricted to the owner customer.")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> toggleFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam boolean isFavorite
    ) {
        log.info("Toggle favorite request for beneficiary ID: {} by customer: {}", id, user.getEmail());
        BeneficiaryResponse existing = beneficiaryService.getBeneficiaryById(id);

        CustomerResponse customerProfile = customerService.getMyProfile(user);
        if (!existing.getCustomerId().equals(customerProfile.getId())) {
            throw new ForbiddenException("Unauthorized: You do not own this beneficiary.");
        }

        BeneficiaryResponse response = beneficiaryService.toggleFavorite(id, isFavorite);
        return ResponseUtil.success("Beneficiary favorite status updated successfully.", response);
    }

    /**
     * Endpoint to block a saved beneficiary.
     */
    @PatchMapping("/{id}/block")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Block beneficiary status", description = "Blocks a saved beneficiary, disabling transfers. Restricted to the owner customer.")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> blockBeneficiary(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Block request for beneficiary ID: {} by customer: {}", id, user.getEmail());
        BeneficiaryResponse existing = beneficiaryService.getBeneficiaryById(id);

        CustomerResponse customerProfile = customerService.getMyProfile(user);
        if (!existing.getCustomerId().equals(customerProfile.getId())) {
            throw new ForbiddenException("Unauthorized: You do not own this beneficiary.");
        }

        BeneficiaryResponse response = beneficiaryService.updateStatus(id, BeneficiaryStatus.BLOCKED);
        return ResponseUtil.success("Beneficiary blocked successfully.", response);
    }

    /**
     * Endpoint to activate a blocked/pending beneficiary.
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Activate beneficiary status", description = "Activates a blocked or pending saved beneficiary. Restricted to the owner customer.")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> activateBeneficiary(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Activation request for beneficiary ID: {} by customer: {}", id, user.getEmail());
        BeneficiaryResponse existing = beneficiaryService.getBeneficiaryById(id);

        CustomerResponse customerProfile = customerService.getMyProfile(user);
        if (!existing.getCustomerId().equals(customerProfile.getId())) {
            throw new ForbiddenException("Unauthorized: You do not own this beneficiary.");
        }

        BeneficiaryResponse response = beneficiaryService.updateStatus(id, BeneficiaryStatus.ACTIVE);
        return ResponseUtil.success("Beneficiary activated successfully.", response);
    }

    /**
     * Endpoint to soft delete a saved beneficiary.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Soft delete saved beneficiary", description = "Soft deletes a saved beneficiary by updating status to DELETED. Restricted to the owner customer.")
    public ResponseEntity<ApiResponse<Void>> deleteBeneficiary(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Delete request for beneficiary ID: {} by customer: {}", id, user.getEmail());
        BeneficiaryResponse existing = beneficiaryService.getBeneficiaryById(id);

        CustomerResponse customerProfile = customerService.getMyProfile(user);
        if (!existing.getCustomerId().equals(customerProfile.getId())) {
            throw new ForbiddenException("Unauthorized: You do not own this beneficiary.");
        }

        beneficiaryService.deleteBeneficiary(id);
        return ResponseUtil.success("Beneficiary deleted successfully.", null);
    }
}
