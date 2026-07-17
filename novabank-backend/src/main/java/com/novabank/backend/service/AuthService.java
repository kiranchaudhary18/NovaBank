package com.novabank.backend.service;

import com.novabank.backend.dto.AuthResponse;
import com.novabank.backend.dto.LoginRequest;
import com.novabank.backend.dto.RegisterRequest;

/**
 * Service interface defining user authentication actions (registration and login).
 *
 * @author Senior Java Backend Architect
 */
public interface AuthService {

    /**
     * Registers a new customer profile, hashes their password, persists records, and generates credentials.
     *
     * @param request registration details
     * @return registration outcome containing JWT access token
     * @throws com.novabank.backend.exception.BadRequestException if email already exists
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user credentials, generates a new JWT access token, and returns profile details.
     *
     * @param request login credentials
     * @return login outcome containing JWT access token
     * @throws com.novabank.backend.exception.BadRequestException on invalid credentials or blocked status
     */
    AuthResponse login(LoginRequest request);
}
