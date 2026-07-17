package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing authorization details returned upon successful registration or login.
 *
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /** The signed JSON Web Token (JWT) bearer value. */
    private String token;

    /** Token type prefix, standard is "Bearer". */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Expiration window remaining in milliseconds. */
    private long expiresIn;

    /** Details of the authenticated user. */
    private UserResponse user;
}
