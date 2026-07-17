package com.novabank.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service providing JSON Web Token (JWT) management tasks, including
 * token generation, validation, parsing, and cryptographic signing.
 * Adheres to JJWT 0.12.x specifications.
 *
 * @author Senior Java Backend Architect
 */
@Service
public class JwtService {

    @Value("${novabank.jwt.secret}")
    private String secretKey;

    @Value("${novabank.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extracts the login username (email) from the token subject.
     *
     * @param token JWT token
     * @return username subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a single custom claim from the JWT token.
     *
     * @param token JWT token
     * @param claimsResolver functional claims resolver callback
     * @param <T> output type
     * @return claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a standard JWT token for the specified user details.
     *
     * @param userDetails user context details
     * @return compact signed JWT string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a signed JWT token containing custom properties.
     *
     * @param extraClaims map containing extra key-values
     * @param userDetails user context details
     * @return compact signed JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Returns the configured token validity window remaining.
     *
     * @return milliseconds window
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Validates whether the token signature is authentic, matches user info, and has not expired.
     *
     * @param token JWT token
     * @param userDetails user security details
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
