package com.novabank.backend.service;

/**
 * Service interface generating formatted HTML templates for notifications emails.
 *
 * @author Senior Java Backend Architect
 */
public interface TemplateService {

    /**
     * Renders a welcome greeting email template.
     *
     * @param fullName customer full name
     * @return HTML email string
     */
    String getWelcomeEmailTemplate(String fullName);

    /**
     * Renders a transaction success alert email template.
     *
     * @param fullName customer full name
     * @param refNumber transaction reference
     * @param type DEPOSIT, WITHDRAW, TRANSFER, etc.
     * @param amount formatted currency amount
     * @param date transaction timestamp
     * @return HTML email string
     */
    String getTransactionSuccessEmailTemplate(String fullName, String refNumber, String type, String amount, String date);

    /**
     * Renders a transaction failure alert email template.
     *
     * @param fullName customer full name
     * @param refNumber transaction reference
     * @param reason error failure reason details
     * @param amount formatted currency amount
     * @return HTML email string
     */
    String getTransactionFailedEmailTemplate(String fullName, String refNumber, String reason, String amount);

    /**
     * Renders a password changed alert email template.
     *
     * @param fullName customer full name
     * @return HTML email string
     */
    String getPasswordChangedEmailTemplate(String fullName);

    /**
     * Renders a card issued alert email template.
     *
     * @param fullName customer full name
     * @param maskedCard masked card number representation
     * @param cardType PHYSICAL or VIRTUAL card
     * @return HTML email string
     */
    String getCardIssuedEmailTemplate(String fullName, String maskedCard, String cardType);

    /**
     * Renders a loan approval alert email template.
     *
     * @param fullName customer full name
     * @param loanAmount approved loan balance
     * @return HTML email string
     */
    String getLoanApprovedEmailTemplate(String fullName, String loanAmount);
}
