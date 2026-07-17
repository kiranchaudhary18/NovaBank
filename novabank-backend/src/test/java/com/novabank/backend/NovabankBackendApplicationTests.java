package com.novabank.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Basic context loads test to verify configuration class bindings
 * and dependency graph compilation using a dummy datasource.
 */
@SpringBootTest
class NovabankBackendApplicationTests {

    @Configuration
    static class TestConfig {
        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            // Configure standard properties; connection is not established during context loading
            // because spring.jpa.hibernate.ddl-auto is set to "none" in test application.properties
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://localhost:5432/novabank");
            dataSource.setUsername("postgres");
            dataSource.setPassword("postgres");
            return dataSource;
        }
    }

    @Test
    void contextLoads() {
        // Verifies context initialization succeeds
    }

}
