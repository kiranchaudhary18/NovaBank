package com.novabank.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Cross-Origin Resource Sharing (CORS) Configuration.
 * Restricts or permits browser-based applications to query APIs.
 *
 * @author Senior Java Backend Architect
 */
@Configuration
public class CorsConfig {

    /**
     * Configures the global CORS filter to permit cross-origin requests.
     * Custom mappings and allowed origins can be adjusted dynamically in properties.
     *
     * @return a configured {@link CorsFilter} bean
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Configure allowed origins (use environment properties in production)
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Origin", "Accept", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // 1 hour preflight cache duration

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
