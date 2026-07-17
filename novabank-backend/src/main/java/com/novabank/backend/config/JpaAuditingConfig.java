package com.novabank.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import java.util.Optional;

/**
 * Enables JPA Auditing and registers the auditor provider bean.
 * Auditing fields like createdBy, updatedBy, createdAt, and updatedAt will be managed automatically.
 *
 * @author Senior Java Backend Architect
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@ConditionalOnProperty(name = "novabank.jpa.auditing.enabled", havingValue = "true", matchIfMissing = true)
public class JpaAuditingConfig {

    /**
     * Auditor provider bean returning the current authenticated username from the Security Context.
     * Defaults to "SYSTEM" when unauthenticated or during background system tasks.
     *
     * @return Auditor identifier
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .or(() -> Optional.of("SYSTEM"));
    }
}
