package com.novabank.backend.service.impl;

import com.novabank.backend.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation compiling HTML emails using key placeholders substitution.
 *
 * @author Senior Java Backend Architect
 */
@Service
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    private static final String EMAIL_STYLE = 
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; padding: 20px; border-radius: 5px;'>" +
            "<div style='background-color: #003366; padding: 15px; text-align: center; border-radius: 5px 5px 0 0;'>" +
            "<h2 style='color: white; margin: 0;'>NovaBank</h2>" +
            "</div>" +
            "<div style='padding: 20px; line-height: 1.6; color: #333333;'>" +
            "{{body}}" +
            "</div>" +
            "<div style='margin-top: 25px; border-top: 1px solid #e0e0e0; padding-top: 10px; font-size: 11px; color: #777777; text-align: center;'>" +
            "This is an automated notification from NovaBank. Please do not reply to this message.<br/>" +
            "&copy; 2026 NovaBank Corp. All rights reserved." +
            "</div>" +
            "</div>";

    @Override
    public String getWelcomeEmailTemplate(String fullName) {
        String body = "<p>Dear <strong>" + fullName + "</strong>,</p>" +
                "<p>Welcome to NovaBank! Your account registration was completed successfully.</p>" +
                "<p>You can now log in to the NovaBank digital portal to verify your KYC details, create savings accounts, request debit cards, and execute money transfers securely.</p>" +
                "<p>Thank you for choosing NovaBank as your trusted financial partner.</p>";
        return EMAIL_STYLE.replace("{{body}}", body);
    }

    @Override
    public String getTransactionSuccessEmailTemplate(String fullName, String refNumber, String type, String amount, String date) {
        String body = "<p>Dear <strong>" + fullName + "</strong>,</p>" +
                "<p>We wish to inform you that a transaction has occurred on your bank account.</p>" +
                "<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Reference Number</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'>" + refNumber + "</td></tr>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Transaction Type</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'>" + type + "</td></tr>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Amount</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2; color: #2e7d32;'><strong>" + amount + "</strong></td></tr>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Timestamp</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'>" + date + "</td></tr>" +
                "</table>" +
                "<p style='margin-top: 15px;'>If you did not authorize this transaction, please contact our support team immediately.</p>";
        return EMAIL_STYLE.replace("{{body}}", body);
    }

    @Override
    public String getTransactionFailedEmailTemplate(String fullName, String refNumber, String reason, String amount) {
        String body = "<p>Dear <strong>" + fullName + "</strong>,</p>" +
                "<p style='color: #c62828;'>A transaction request on your account has failed.</p>" +
                "<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Reference Number</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'>" + refNumber + "</td></tr>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Amount Tried</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'>" + amount + "</td></tr>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Decline Reason</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2; color: #c62828;'>" + reason + "</td></tr>" +
                "</table>" +
                "<p style='margin-top: 15px;'>If you need assistance, please verify your account limits or contact customer support.</p>";
        return EMAIL_STYLE.replace("{{body}}", body);
    }

    @Override
    public String getPasswordChangedEmailTemplate(String fullName) {
        String body = "<p>Dear <strong>" + fullName + "</strong>,</p>" +
                "<p>This is a security alert to confirm that the password for your NovaBank digital login was changed successfully.</p>" +
                "<p><strong>If you initiated this change:</strong> You can safely ignore this notification.</p>" +
                "<p style='color: #c62828; font-weight: bold;'>If you did NOT change your password: Please contact NovaBank fraud support immediately to lock your credentials.</p>";
        return EMAIL_STYLE.replace("{{body}}", body);
    }

    @Override
    public String getCardIssuedEmailTemplate(String fullName, String maskedCard, String cardType) {
        String body = "<p>Dear <strong>" + fullName + "</strong>,</p>" +
                "<p>A new <strong>" + cardType + "</strong> card has been successfully issued to your bank account.</p>" +
                "<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Masked Card Number</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'>" + maskedCard + "</td></tr>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Card Type</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'>" + cardType + "</td></tr>" +
                "</table>" +
                "<p style='margin-top: 15px;'>Please complete your card activation by logging in and setting up your secure contactless limits and international options.</p>";
        return EMAIL_STYLE.replace("{{body}}", body);
    }

    @Override
    public String getLoanApprovedEmailTemplate(String fullName, String loanAmount) {
        String body = "<p>Dear <strong>" + fullName + "</strong>,</p>" +
                "<p>We are pleased to inform you that your credit loan application has been approved.</p>" +
                "<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>" +
                "<tr><td style='padding: 8px; border-bottom: 1px solid #f2f2f2;'><strong>Approved Balance</strong></td><td style='padding: 8px; border-bottom: 1px solid #f2f2f2; color: #2e7d32;'><strong>" + loanAmount + "</strong></td></tr>" +
                "</table>" +
                "<p style='margin-top: 15px;'>The approved funds will be credited to your primary savings account shortly. Our tellers will contact you with the repayment schedules details.</p>";
        return EMAIL_STYLE.replace("{{body}}", body);
    }
}
