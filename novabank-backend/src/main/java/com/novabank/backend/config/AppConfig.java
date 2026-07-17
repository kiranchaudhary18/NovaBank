package com.novabank.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * General application configurations and third-party bean registrations.
 *
 * @author Senior Java Backend Architect
 */
@Configuration
public class AppConfig {

    /**
     * BCrypt Password Encoder bean utilized for secure credential hashing.
     * Placed in foundation configuration to resolve security dependencies.
     *
     * @return {@link PasswordEncoder} implementation using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
