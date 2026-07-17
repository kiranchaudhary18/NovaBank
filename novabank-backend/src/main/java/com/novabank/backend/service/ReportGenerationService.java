package com.novabank.backend.service;

/**
 * Service interface executing automated reports assemblies.
 *
 * @author Senior Java Backend Architect
 */
public interface ReportGenerationService {

    /**
     * Compiles daily transactional audits, loan updates, and revenue metrics.
     */
    void generateDailyReports();

    /**
     * Compiles monthly aggregates analytics and registrations reports.
     */
    void generateMonthlyReports();
}
