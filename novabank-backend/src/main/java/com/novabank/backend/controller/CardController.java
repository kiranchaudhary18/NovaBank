package com.novabank.backend.controller;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.CardNetwork;
import com.novabank.backend.enums.CardStatus;
import com.novabank.backend.enums.CardType;
import com.novabank.backend.exception.ForbiddenException;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.CardService;
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

import java.util.List;
import java.util.UUID;

/**
 * Controller exposing REST API endpoints for Debit & Virtual Card Management.
 * Path mapping: "/api/v1/cards". Protected by stateless JWT authorizations.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Debit & Virtual Card Management Module", description = "APIs to issue physical/virtual cards, manage ATM/online limits, update PIN codes, and block/freeze cards")
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService cardService;
    private final CustomerService customerService;

    /**
     * Endpoint to issue a physical debit card (Employee / Admin restricted).
     */
    @PostMapping("/issue")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Issue a physical debit card", description = "Deploys a physical card linked to a bank account. Enforces one physical card limit. Restricted to Admin/Employee.")
    public ResponseEntity<ApiResponse<CardResponse>> issuePhysicalCard(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody IssueCardRequest request
    ) {
        log.info("Request to issue physical card by teller: {}", user.getEmail());
        CardResponse response = cardService.issuePhysicalCard(user, request);
        return ResponseUtil.created("Physical debit card issued successfully.", response);
    }

    /**
     * Endpoint to request a virtual debit card.
     */
    @PostMapping("/virtual")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Issue a virtual debit card", description = "Registers a virtual card linked to a bank account. Customers can only register cards for their own accounts.")
    public ResponseEntity<ApiResponse<CardResponse>> issueVirtualCard(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody VirtualCardRequest request
    ) {
        log.info("Request to issue virtual card by user: {}", user.getEmail());
        CardResponse response = cardService.issueVirtualCard(user, request);
        return ResponseUtil.created("Virtual card registered successfully.", response);
    }

    /**
     * Endpoint to fetch details of a specific card by its UUID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get card details by ID", description = "Retrieves details of an issued card. Customers can only view cards they own.")
    public ResponseEntity<ApiResponse<CardResponse>> getCardById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Request to fetch card ID: {} by user: {}", id, user.getEmail());
        CardResponse response = cardService.getCardById(id);

        // Security check: Customer can only view details of cards they own
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            if (!response.getCustomerId().equals(customerProfile.getId())) {
                throw new ForbiddenException("Access Denied: You do not own this card.");
            }
        }

        return ResponseUtil.success("Card details retrieved successfully.", response);
    }

    /**
     * Endpoint to list cards linked to a specific account number.
     */
    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Get cards by Account Number", description = "Retrieves all physical and virtual cards linked to a bank account. Customers can only view cards for accounts they own.")
    public ResponseEntity<ApiResponse<List<CardResponse>>> getCardsByAccountNumber(
            @AuthenticationPrincipal User user,
            @PathVariable String accountNumber
    ) {
        log.info("Request to list cards for account: {} by user: {}", accountNumber, user.getEmail());
        List<CardResponse> response = cardService.getCardsByAccountNumber(user, accountNumber);
        return ResponseUtil.success("Cards retrieved successfully.", response);
    }

    /**
     * Endpoint to list and filter cards dynamically.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "List and search cards ledger (Paginated)", description = "Searches and filters card records. Customers can only view their own cards; Employees/Admins can search all.")
    public ResponseEntity<ApiResponse<PagedResponse<CardResponse>>> searchCards(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) CardType cardType,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) CardNetwork network
    ) {
        log.info("Search request for cards by user: {}", user.getEmail());
        UUID customerIdFilter;

        // Security check: Customer can only list cards they own
        if (user.getRole().getRoleName().name().equals("ROLE_CUSTOMER")) {
            CustomerResponse customerProfile = customerService.getMyProfile(user);
            customerIdFilter = customerProfile.getId();
        } else {
            customerIdFilter = customerId;
        }

        PagedResponse<CardResponse> response = cardService.searchCards(
                page, size, sortBy, sortDir, cardNumber, accountNumber, customerIdFilter, cardType, status, network
        );
        return ResponseUtil.success("Cards list retrieved successfully.", response);
    }

    /**
     * Endpoint to freeze card.
     */
    @PatchMapping("/freeze/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Freeze card", description = "Suspends card usages temporarily. Card status changes to FROZEN.")
    public ResponseEntity<ApiResponse<CardResponse>> freezeCard(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Freeze request for card ID: {} by user: {}", id, user.getEmail());
        CardResponse response = cardService.freezeCard(user, id);
        return ResponseUtil.success("Card frozen successfully.", response);
    }

    /**
     * Endpoint to unfreeze card.
     */
    @PatchMapping("/unfreeze/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Unfreeze card", description = "Re-activates a frozen card.")
    public ResponseEntity<ApiResponse<CardResponse>> unfreezeCard(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        log.info("Unfreeze request for card ID: {} by user: {}", id, user.getEmail());
        CardResponse response = cardService.unfreezeCard(user, id);
        return ResponseUtil.success("Card unfrozen successfully.", response);
    }

    /**
     * Endpoint to permanently block card (Employee / Admin restricted).
     */
    @PatchMapping("/block/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Block card permanently", description = "Blocks card usage permanently due to theft, loss, etc. Card status changes to BLOCKED. Restricted to Employee/Admin.")
    public ResponseEntity<ApiResponse<CardResponse>> blockCard(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam String reason
    ) {
        log.info("Block request for card ID: {} by teller: {}", id, user.getEmail());
        CardResponse response = cardService.blockCard(user, id, reason);
        return ResponseUtil.success("Card blocked permanently.", response);
    }

    /**
     * Endpoint to replace a card (Admin restricted).
     */
    @PatchMapping("/replace/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Replace blocked/expired card", description = "Generates a replacement card for an expired or blocked card, carrying over limits. Restricted to Admin.")
    public ResponseEntity<ApiResponse<CardResponse>> replaceCard(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam String newPin
    ) {
        log.info("Replacement request for card ID: {} by admin: {}", id, user.getEmail());
        CardResponse response = cardService.replaceCard(user, id, newPin);
        return ResponseUtil.success("Replacement card issued successfully.", response);
    }

    /**
     * Endpoint to change/reset PIN code.
     */
    @PatchMapping("/change-pin")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @Operation(summary = "Change card PIN code", description = "Modifies the card's active security PIN. Enforces validation of current PIN code.")
    public ResponseEntity<ApiResponse<CardResponse>> changePin(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PinChangeRequest request
    ) {
        log.info("PIN change request by card owner: {}", user.getEmail());
        CardResponse response = cardService.changePin(user, request);
        return ResponseUtil.success("PIN changed successfully.", response);
    }

    /**
     * Endpoint to update spending limits.
     */
    @PatchMapping("/update-limits")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Update card limits", description = "Configures custom ATM, POS, and online transaction bounds.")
    public ResponseEntity<ApiResponse<CardResponse>> updateLimits(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateCardLimitRequest request
    ) {
        log.info("Limits update request by user: {}", user.getEmail());
        CardResponse response = cardService.updateLimits(user, request);
        return ResponseUtil.success("Limits updated successfully.", response);
    }

    /**
     * Endpoint to toggle contactless permission.
     */
    @PatchMapping("/contactless")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Toggle contactless payments", description = "Enables or disables contactless payments on a card.")
    public ResponseEntity<ApiResponse<CardResponse>> toggleContactless(
            @AuthenticationPrincipal User user,
            @RequestParam UUID id,
            @RequestParam boolean enabled
    ) {
        log.info("Contactless toggle request to {} by user: {}", enabled, user.getEmail());
        CardResponse response = cardService.toggleContactless(user, id, enabled);
        return ResponseUtil.success("Contactless payment settings updated.", response);
    }

    /**
     * Endpoint to toggle international permission.
     */
    @PatchMapping("/international")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
    @Operation(summary = "Toggle international usage", description = "Enables or disables international payments on a card.")
    public ResponseEntity<ApiResponse<CardResponse>> toggleInternational(
            @AuthenticationPrincipal User user,
            @RequestParam UUID id,
            @RequestParam boolean enabled
    ) {
        log.info("International toggle request to {} by user: {}", enabled, user.getEmail());
        CardResponse response = cardService.toggleInternational(user, id, enabled);
        return ResponseUtil.success("International payment settings updated.", response);
    }
}
