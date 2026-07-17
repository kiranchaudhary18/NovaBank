package com.novabank.backend.controller;

import com.novabank.backend.dto.AuthResponse;
import com.novabank.backend.dto.LoginRequest;
import com.novabank.backend.dto.RegisterRequest;
import com.novabank.backend.dto.UserResponse;
import com.novabank.backend.entity.User;
import com.novabank.backend.response.ApiResponse;
import com.novabank.backend.service.AuthService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing authentication and authorization REST endpoints.
 * Context path "/api/v1" is prefixed globally via properties.
 *
 * @author Senior Java Backend Architect
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication & Authorization Module", description = "APIs for user registration, token-based login, and caller profile lookup")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Endpoint to register a new customer user account.
     *
     * @param request registration input payload
     * @return 201 Created containing JWT access token and user info
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new customer profile",
            description = "Creates a new customer user profile in the system with default ROLE_CUSTOMER privileges and returns a JWT access token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully, profile and token returned."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload details or email address is already in use.")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST request to register user with email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseUtil.created("User registered successfully.", response);
    }

    /**
     * Endpoint to authenticate user credentials and generate JWT access token.
     *
     * @param request login credential payload
     * @return 200 OK containing JWT access token and user info
     */
    @PostMapping("/login")
    @Operation(
            summary = "User login authentication",
            description = "Authenticates credentials (email and password) and generates a stateless JWT bearer token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authentication successful, token and user profile returned."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid login credentials (email or password), or account is blocked.")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login user with email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseUtil.success("Authentication successful.", response);
    }

    /**
     * Endpoint to retrieve the currently logged in user profile details.
     * Requires JWT Authorization Bearer header.
     *
     * @param user authenticated User context principal injected by Spring Security
     * @return 200 OK containing caller user profile info
     */
    @GetMapping("/me")
    @Operation(
            summary = "Get current authenticated user profile",
            description = "Retrieves profile details of the currently authenticated caller using the Authorization bearer token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile details retrieved successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Caller is unauthenticated, missing or invalid token.")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            log.warn("GET /me requested but principal is empty");
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "Caller is unauthenticated.");
        }
        log.info("REST request to get current user details for: {}", user.getEmail());
        UserResponse userResponse = userService.convertToUserResponse(user);
        return ResponseUtil.success("Current user profile retrieved successfully.", userResponse);
    }
}
