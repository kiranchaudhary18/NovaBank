package com.novabank.backend.service;

/**
 * Service interface generating downloadable CSV and PDF report tables.
 *
 * @author Senior Java Backend Architect
 */
public interface ReportService {

    /**
     * Compiles customer profiles logs into report buffers.
     *
     * @param format "CSV" or "PDF"
     * @param status customer active/inactive status filter (optional)
     * @param dateRange filter by registration date (optional)
     * @return binary array report document
     */
    byte[] generateCustomerReport(String format, String status, String dateRange);

    /**
     * Compiles account ledgers into report buffers.
     *
     * @param format "CSV" or "PDF"
     * @param status account active/frozen status filter (optional)
     * @param dateRange filter by creation date (optional)
     * @return binary array report document
     */
    byte[] generateAccountReport(String format, String status, String dateRange);

    /**
     * Compiles transaction logs into report buffers.
     *
     * @param format "CSV" or "PDF"
     * @param type DEPOSIT, WITHDRAW, TRANSFER filter (optional)
     * @param dateRange filter by transaction timestamp (optional)
     * @return binary array report document
     */
    byte[] generateTransactionReport(String format, String type, String dateRange);

    /**
     * Generates a placeholder report for loans.
     *
     * @param format "CSV" or "PDF"
     * @param status loan status (optional)
     * @param dateRange date range filter (optional)
     * @return binary array report document
     */
    byte[] generateLoanReport(String format, String status, String dateRange);

    /**
     * Compiles platform fee revenue summaries into report buffers.
     *
     * @param format "CSV" or "PDF"
     * @param dateRange date range filter (optional)
     * @return binary array report document
     */
    byte[] generateRevenueReport(String format, String dateRange);
}
