package com.novabank.backend.service;

import com.novabank.backend.dto.DashboardResponse;

/**
 * Service interface compiling overarching KPIs and statistics for administrative dashboards.
 *
 * @author Senior Java Backend Architect
 */
public interface DashboardService {

    /**
     * Retrieves aggregated indicators (totals, active sessions, today's volumes, etc.).
     *
     * @return DashboardResponse aggregated dataset
     */
    DashboardResponse getDashboardStats();
}
