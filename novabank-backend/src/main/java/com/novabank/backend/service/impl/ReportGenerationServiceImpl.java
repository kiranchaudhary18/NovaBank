package com.novabank.backend.service.impl;

import com.novabank.backend.service.ReportGenerationService;
import com.novabank.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation executing automated daily and monthly reports compilations.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationServiceImpl implements ReportGenerationService {

    private final ReportService reportService;

    @Override
    public void generateDailyReports() {
        log.info("Starting Daily Administrative Reports generation batch");
        try {
            byte[] txns = reportService.generateTransactionReport("CSV", null, null);
            byte[] revenue = reportService.generateRevenueReport("CSV", null);
            log.info("Daily reports compiled successfully. Transaction CSV size: {} | Revenue CSV size: {}", txns.length, revenue.length);
        } catch (Exception e) {
            log.error("Failed to generate daily reports batch: ", e);
        }
    }

    @Override
    public void generateMonthlyReports() {
        log.info("Starting Monthly Administrative Reports generation batch");
        try {
            byte[] customers = reportService.generateCustomerReport("PDF", null, null);
            log.info("Monthly reports compiled successfully. Customer PDF size: {}", customers.length);
        } catch (Exception e) {
            log.error("Failed to generate monthly reports batch: ", e);
        }
    }
}
