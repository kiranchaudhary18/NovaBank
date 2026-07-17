package com.novabank.backend.controller;

import com.novabank.backend.dto.*;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.enums.UserStatus;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.UserService;
import com.novabank.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller exposing user profile and user administration REST endpoints.
 * Path routing: "/api/v1/users". Protected by stateless JWT authorizations.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management Module", description = "APIs for user profile management and administrative actions")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves the profile details of the authenticated caller.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current caller user details", description = "Retrieves profile details of the authenticated caller.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token is missing or expired.")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal User user) {
        log.info("Request to fetch current user profile: {}", user.getEmail());
        UserResponse response = userService.convertToUserResponse(user);
        return ResponseUtil.success("Profile details retrieved successfully.", response);
    }

    /**
     * Updates profile details of the authenticated caller.
     */
    @PutMapping("/profile")
    @Operation(summary = "Update current caller profile details", description = "Updates full name and phone number of the currently logged-in user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid validation inputs or phone number already exists.")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("Request to update profile for user: {}", user.getEmail());
        UserResponse response = userService.updateProfile(user.getId(), request);
        return ResponseUtil.success("Profile updated successfully.", response);
    }

    /**
     * Changes password of the authenticated caller.
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change current user password", description = "Changes password for current caller after verifying old password.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password updated successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Old password doesn't match, or new password fails validation constraints.")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("Request to change password for user: {}", user.getEmail());
        userService.changePassword(user.getId(), request);
        return ResponseUtil.success("Password changed successfully.", null);
    }

    /**
     * Retrieves user by UUID. Accessible by ADMIN, or the user themselves.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.name == @userServiceImpl.getUserById(#id).email")
    @Operation(summary = "Get user profile details by ID", description = "Retrieves user profile details by ID. Accessible by ADMIN or the user themselves.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Caller is unauthorized to view this profile."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found or is soft-deleted.")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        log.info("Request to fetch user details for ID: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseUtil.success("User retrieved successfully.", response);
    }

    /**
     * Lists all users. Accessible by ADMIN and EMPLOYEE roles only.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Get all users (Paginated)", description = "Retrieves lists of all users. Excludes soft-deleted records. Accessible by ADMIN and EMPLOYEE only.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Caller lacks administrative/employee permissions.")
    })
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        log.info("Request to fetch all users. Page: {}, Size: {}", page, size);
        PagedResponse<UserResponse> response = userService.getAllUsers(page, size, sortBy, sortDir);
        return ResponseUtil.success("Users list retrieved successfully.", response);
    }

    /**
     * Search users by criteria. Accessible by ADMIN and EMPLOYEE roles only.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @Operation(summary = "Search users dynamically (Paginated)", description = "Searches users dynamically based on name, email, phone, status, and role. Accessible by ADMIN and EMPLOYEE only.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search query completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Caller lacks administrative/employee permissions.")
    })
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) RoleType role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        UserFilterRequest filter = UserFilterRequest.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .status(status)
                .role(role)
                .build();
        log.info("Request to search users with filters: {}", filter);
        PagedResponse<UserResponse> response = userService.searchUsers(filter, page, size, sortBy, sortDir);
        return ResponseUtil.success("Search completed successfully.", response);
    }

    /**
     * Deactivate user profile. Accessible by ADMIN only.
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Deactivate user account status", description = "Deactivates a user profile by changing status to INACTIVE. Accessible by ADMIN only.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deactivated successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Caller lacks ADMIN permissions.")
    })
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable UUID id) {
        log.info("Request to deactivate user ID: {}", id);
        UserResponse response = userService.updateStatus(id, UserStatus.INACTIVE);
        return ResponseUtil.success("User account deactivated successfully.", response);
    }

    /**
     * Activate user profile. Accessible by ADMIN only.
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Activate user account status", description = "Activates a user profile by changing status to ACTIVE. Accessible by ADMIN only.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User activated successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Caller lacks ADMIN permissions.")
    })
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable UUID id) {
        log.info("Request to activate user ID: {}", id);
        UserResponse response = userService.updateStatus(id, UserStatus.ACTIVE);
        return ResponseUtil.success("User account activated successfully.", response);
    }

    /**
     * Soft delete user profile. Accessible by ADMIN only.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Soft delete user profile", description = "Soft deletes a user profile by changing status state to DELETED. Accessible by ADMIN only.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User soft deleted successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Caller lacks ADMIN permissions.")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        log.info("Request to soft-delete user ID: {}", id);
        userService.softDelete(id);
        return ResponseUtil.success("User account soft deleted successfully.", null);
    }
}
