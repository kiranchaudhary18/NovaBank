package com.novabank.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main Spring Security configurations class.
 * Configures stateless web security policy, permit-all route paths,
 * authentication providers, and filters.
 *
 * @author Senior Java Backend Architect
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Configures the main HTTP security filter chain.
     *
     * @param http HttpSecurity configuration builder
     * @return constructed SecurityFilterChain
     * @throws Exception configuration exceptions
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF as JWT is stateless and not cookie-based
            .csrf(AbstractHttpConfigurer::disable)
            // Enable default CORS configuration mapping (picks up the CorsFilter bean)
            .cors(cors -> {})
            // Configure route authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow public access to all auth API routes
                .requestMatchers("/auth/**").permitAll()
                // Allow public access to Swagger UI and OpenApi docs
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()
                // Require authentication for all other backend routes
                .anyRequest().authenticated()
            )
            // Configure stateless session management (no server-side HTTP Sessions)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Register authentication provider
            .authenticationProvider(authenticationProvider())
            // Add custom JWT filter before the standard authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures DaoAuthenticationProvider using customized user service and password encoder.
     *
     * @return AuthenticationProvider bean
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Exposes the AuthenticationManager bean from Spring configuration.
     * Required in AuthService implementation to validate login attempts.
     *
     * @param config standard authentication configuration context
     * @return AuthenticationManager instance
     * @throws Exception if retrieving manager fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
