package com.novabank.backend.service;

/**
 * Service interface defining HTML email dispatch operations.
 *
 * @author Senior Java Backend Architect
 */
public interface EmailService {

    /**
     * Dispatches a structured HTML email message.
     * Must execute asynchronously to prevent locking bank databases transactions thread processing.
     *
     * @param to recipient email address
     * @param subject email subject line
     * @param htmlContent compiled HTML layout body
     */
    void sendHtmlEmail(String to, String subject, String htmlContent);
}
