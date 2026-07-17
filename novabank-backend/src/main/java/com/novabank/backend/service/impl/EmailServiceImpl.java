package com.novabank.backend.service.impl;

import com.novabank.backend.exception.TransactionFailedException;
import com.novabank.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service implementation managing SMTP email dispatches.
 * Executed asynchronously to guarantee non-blocking banking operations.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async("taskExecutor")
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        log.info("Sending asynchronous HTML email to: {} with subject: '{}' on thread: {}", 
                to, subject, Thread.currentThread().getName());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true sets HTML layout parsing

            mailSender.send(message);
            log.info("Asynchronous email dispatched successfully to: {}", to);
        } catch (MessagingException | org.springframework.mail.MailException exception) {
            log.error("Failed to compile or dispatch SMTP email to recipient: " + to, exception);
            // Since this runs asynchronously on a separate thread, throwing an exception 
            // does not interrupt the main transaction context thread, but acts as a robust logger report.
        }
    }
}
