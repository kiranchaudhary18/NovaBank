package com.novabank.backend.service.impl;

import com.novabank.backend.dto.AuthResponse;
import com.novabank.backend.dto.LoginRequest;
import com.novabank.backend.dto.RegisterRequest;
import com.novabank.backend.dto.UserResponse;
import com.novabank.backend.entity.Role;
import com.novabank.backend.entity.User;
import com.novabank.backend.enums.RoleType;
import com.novabank.backend.enums.UserStatus;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.exception.ResourceNotFoundException;
import com.novabank.backend.exception.ConflictException;
import com.novabank.backend.exception.UnauthorizedException;
import com.novabank.backend.repository.RoleRepository;
import com.novabank.backend.repository.UserRepository;
import com.novabank.backend.security.JwtService;
import com.novabank.backend.service.AuthService;
import com.novabank.backend.service.EventPublisherService;
import com.novabank.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for handling authentication tasks (registration and login).
 * Uses constructor-based injection for collaborating components.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final EventPublisherService eventPublisherService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Initiating user registration process for email: {}", request.getEmail());

        // 1. Verify duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new ConflictException("An account is already registered with this email address.");
        }

        // 2. Fetch appropriate role
        RoleType roleType = RoleType.ROLE_CUSTOMER;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                String requestedRole = request.getRole().toUpperCase().trim();
                if (!requestedRole.startsWith("ROLE_")) {
                    requestedRole = "ROLE_" + requestedRole;
                }
                roleType = RoleType.valueOf(requestedRole);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role requested: {}, throwing bad request", request.getRole());
                throw new BadRequestException("Invalid role specified: " + request.getRole());
            }
        }

        final RoleType finalRoleType = roleType;
        Role resolvedRole = roleRepository.findByRoleName(finalRoleType)
                .orElseThrow(() -> {
                    log.error("Fatal: Role {} not found in database.", finalRoleType);
                    return new ResourceNotFoundException("Requested role could not be resolved.");
                });

        // 3. Construct and persist User entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(resolvedRole)
                .status(UserStatus.ACTIVE) // Activated by default for phase 2 ease
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Publish event to trigger welcome email asynchronously
        eventPublisherService.publishUserRegisteredEvent(savedUser);

        // 4. Generate access token and build DTO payload
        String jwtToken = jwtService.generateToken(savedUser);
        UserResponse userResponse = userService.convertToUserResponse(savedUser);

        return AuthResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Initiating login process for email: {}", request.getEmail());

        // 1. Authenticate credentials via Spring AuthenticationManager
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException exception) {
            log.warn("Login failed: Invalid credentials for email: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password.");
        }

        // 2. Fetch authenticated entity record
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found."));

        // 3. Enforce account status check
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Login failed: Account status is {} for email: {}", user.getStatus(), request.getEmail());
            throw new BadRequestException("Your account is currently " + user.getStatus().name().toLowerCase() + ". Please contact support.");
        }

        log.info("User {} authenticated successfully", request.getEmail());

        // 4. Generate access token and compile DTO payload
        String jwtToken = jwtService.generateToken(user);
        UserResponse userResponse = userService.convertToUserResponse(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .user(userResponse)
                .build();
    }
}
