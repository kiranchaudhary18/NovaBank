package com.novabank.backend.service;

/**
 * Service interface executing automated maintenance cleanup tasks.
 *
 * @author Senior Java Backend Architect
 */
public interface CleanupService {

    /**
     * Purges expired logs, tokens, and temporary files from DB storage.
     *
     * @return count of deleted logs rows
     */
    int cleanOldLogs();
}
