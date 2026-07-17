package com.novabank.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Thread Pool Configuration to handle async operations such as email sends.
 * Annotates context with {@link EnableAsync} to enable asynchronous message handlers.
 *
 * @author Senior Java Backend Architect
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Initializes the default thread task executor.
     *
     * @return Thread pool executor configurations
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);        // Minimum thread pool size
        executor.setMaxPoolSize(15);        // Maximum boundary limits
        executor.setQueueCapacity(500);     // Queue limits before thread allocation increases
        executor.setThreadNamePrefix("NovaBank-Async-");
        executor.initialize();
        return executor;
    }
}
