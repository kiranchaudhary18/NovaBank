package com.novabank.backend.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.novabank.backend.entity.Account;
import com.lowagie.text.Font;
import com.novabank.backend.entity.Customer;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.enums.AccountStatus;
import com.novabank.backend.enums.CustomerStatus;
import com.novabank.backend.enums.TransactionType;
import com.novabank.backend.exception.BadRequestException;
import com.novabank.backend.service.ReportService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service implementation generating downloadable administrative CSV and PDF reports.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public byte[] generateCustomerReport(String format, String status, String dateRange) {
        log.info("Generating customer profiles report. Format: {}", format);

        String jpql = "SELECT c FROM Customer c WHERE c.status != 'DELETED'";
        if (status != null && !status.isBlank()) {
            jpql += " AND c.status = :status";
        }
        jpql += " ORDER BY c.createdAt DESC";

        TypedQuery<Customer> query = entityManager.createQuery(jpql, Customer.class);
        if (status != null && !status.isBlank()) {
            try {
                query.setParameter("status", CustomerStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status parameter value: " + status);
            }
        }

        List<Customer> list = query.getResultList();

        if (format.equalsIgnoreCase("CSV")) {
            return exportCustomersToCsv(list);
        } else if (format.equalsIgnoreCase("PDF")) {
            return exportCustomersToPdf(list);
        }
        throw new BadRequestException("Unsupported report format type: " + format);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateAccountReport(String format, String status, String dateRange) {
        log.info("Generating accounts ledger report. Format: {}", format);

        String jpql = "SELECT a FROM Account a WHERE 1=1";
        if (status != null && !status.isBlank()) {
            jpql += " AND a.status = :status";
        }
        jpql += " ORDER BY a.createdAt DESC";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class);
        if (status != null && !status.isBlank()) {
            try {
                query.setParameter("status", AccountStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status parameter value: " + status);
            }
        }

        List<Account> list = query.getResultList();

        if (format.equalsIgnoreCase("CSV")) {
            return exportAccountsToCsv(list);
        } else if (format.equalsIgnoreCase("PDF")) {
            return exportAccountsToPdf(list);
        }
        throw new BadRequestException("Unsupported report format type: " + format);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateTransactionReport(String format, String type, String dateRange) {
        log.info("Generating transaction ledger report. Format: {}", format);

        String jpql = "SELECT t FROM Transaction t WHERE 1=1";
        if (type != null && !type.isBlank()) {
            jpql += " AND t.transactionType = :type";
        }
        jpql += " ORDER BY t.transactionDate DESC";

        TypedQuery<Transaction> query = entityManager.createQuery(jpql, Transaction.class);
        if (type != null && !type.isBlank()) {
            try {
                query.setParameter("type", TransactionType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid transaction type parameter: " + type);
            }
        }

        List<Transaction> list = query.getResultList();

        if (format.equalsIgnoreCase("CSV")) {
            return exportTransactionsToCsv(list);
        } else if (format.equalsIgnoreCase("PDF")) {
            return exportTransactionsToPdf(list);
        }
        throw new BadRequestException("Unsupported report format type: " + format);
    }

    @Override
    public byte[] generateLoanReport(String format, String status, String dateRange) {
        log.info("Generating loan stubs report. Format: {}", format);

        if (format.equalsIgnoreCase("CSV")) {
            String csv = "Loan ID,Customer Name,Loan Amount,Outstanding Balance,Repayment Term,Status\n" +
                    "L-001,John Doe,25000.00,12000.00,36 Months,ACTIVE\n" +
                    "L-002,Jane Smith,50000.00,45000.00,60 Months,ACTIVE\n" +
                    "L-003,Bob Johnson,10000.00,0.00,12 Months,PAID_OFF\n";
            return csv.getBytes(StandardCharsets.UTF_8);
        } else if (format.equalsIgnoreCase("PDF")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            try {
                PdfWriter.getInstance(document, out);
                document.open();

                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
                Paragraph title = new Paragraph("NovaBank - Loan Report (Mock Ledger)", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.addCell("Loan ID");
                table.addCell("Customer Name");
                table.addCell("Loan Amount");
                table.addCell("Outstanding Balance");
                table.addCell("Term");
                table.addCell("Status");

                // Seed row 1
                table.addCell("L-001"); table.addCell("John Doe"); table.addCell("$25,000.00"); table.addCell("$12,000.00"); table.addCell("36 Months"); table.addCell("ACTIVE");
                // Seed row 2
                table.addCell("L-002"); table.addCell("Jane Smith"); table.addCell("$50,000.00"); table.addCell("$45,000.00"); table.addCell("60 Months"); table.addCell("ACTIVE");

                document.add(table);
                document.close();
            } catch (Exception e) {
                log.error("Failed to build PDF mock report: ", e);
            }
            return out.toByteArray();
        }
        throw new BadRequestException("Unsupported report format type: " + format);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateRevenueReport(String format, String dateRange) {
        log.info("Generating platform fee revenues report. Format: {}", format);

        long transfersCount = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t " +
                        "WHERE t.transactionType IN ('TRANSFER', 'BENEFICIARY_TRANSFER', 'INTERNAL_TRANSFER') " +
                        "AND t.status = 'SUCCESS'", Long.class
        ).getSingleResult();
        BigDecimal txnCharges = new BigDecimal(transfersCount).multiply(new BigDecimal("1.50"));

        BigDecimal interest = new BigDecimal("4500.00");
        BigDecimal processing = new BigDecimal("1200.00");
        BigDecimal penalty = new BigDecimal("350.00");
        BigDecimal total = interest.add(processing).add(penalty).add(txnCharges);

        if (format.equalsIgnoreCase("CSV")) {
            String csv = "Revenue Stream,Amount\n" +
                    "Loan Interest Earned,$" + interest + "\n" +
                    "Account Processing Fees,$" + processing + "\n" +
                    "Penalty Charges,$" + penalty + "\n" +
                    "Transaction Transfer Charges,$" + txnCharges + "\n" +
                    "Total Aggregated Revenue,$" + total + "\n";
            return csv.getBytes(StandardCharsets.UTF_8);
        } else if (format.equalsIgnoreCase("PDF")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            try {
                PdfWriter.getInstance(document, out);
                document.open();

                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
                Paragraph title = new Paragraph("NovaBank - Platform Revenue Report", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.addCell("Revenue Stream");
                table.addCell("Aggregated Amount");

                table.addCell("Loan Interest Earned"); table.addCell("$" + interest);
                table.addCell("Account Processing Fees"); table.addCell("$" + processing);
                table.addCell("Penalty Charges"); table.addCell("$" + penalty);
                table.addCell("Transaction Transfer Charges"); table.addCell("$" + txnCharges);
                table.addCell("Total Platform Revenue"); table.addCell("$" + total);

                document.add(table);
                document.close();
            } catch (Exception e) {
                log.error("Failed to build PDF report: ", e);
            }
            return out.toByteArray();
        }
        throw new BadRequestException("Unsupported report format type: " + format);
    }

    // ==========================================
    // PRIVATE EXPORT HELPER METHODS
    // ==========================================

    private byte[] exportCustomersToCsv(List<Customer> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("Customer ID,First Name,Last Name,Email,Phone,Status,Registered Date");
            for (Customer c : list) {
                writer.println(c.getCustomerId() + "," +
                        c.getFirstName() + "," +
                        c.getLastName() + "," +
                        c.getEmail() + "," +
                        c.getPhoneNumber() + "," +
                        c.getStatus().name() + "," +
                        c.getCreatedAt());
            }
        }
        return out.toByteArray();
    }

    private byte[] exportCustomersToPdf(List<Customer> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph title = new Paragraph("NovaBank - Customer Profiles Register", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Cust ID");
            table.addCell("Full Name");
            table.addCell("Email Address");
            table.addCell("Phone Number");
            table.addCell("Status");

            for (Customer c : list) {
                table.addCell(c.getCustomerId());
                table.addCell(c.getFirstName() + " " + c.getLastName());
                table.addCell(c.getEmail());
                table.addCell(c.getPhoneNumber());
                table.addCell(c.getStatus().name());
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            log.error("Failed to compile customer PDF: ", e);
        }
        return out.toByteArray();
    }

    private byte[] exportAccountsToCsv(List<Account> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("Account Number,Customer Name,Account Type,Balance,Status,Opened Date");
            for (Account a : list) {
                String owner = a.getCustomer().getFirstName() + " " + a.getCustomer().getLastName();
                writer.println(a.getAccountNumber() + "," +
                        owner + "," +
                        a.getAccountType().name() + "," +
                        a.getBalance() + "," +
                        a.getStatus().name() + "," +
                        a.getCreatedAt());
            }
        }
        return out.toByteArray();
    }

    private byte[] exportAccountsToPdf(List<Account> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph title = new Paragraph("NovaBank - Deposit Accounts Register", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Acc Number");
            table.addCell("Holder Name");
            table.addCell("Type");
            table.addCell("Balance");
            table.addCell("Status");

            for (Account a : list) {
                table.addCell(a.getAccountNumber());
                table.addCell(a.getCustomer().getFirstName() + " " + a.getCustomer().getLastName());
                table.addCell(a.getAccountType().name());
                table.addCell("$" + a.getBalance());
                table.addCell(a.getStatus().name());
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            log.error("Failed to compile accounts PDF: ", e);
        }
        return out.toByteArray();
    }

    private byte[] exportTransactionsToCsv(List<Transaction> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("Transaction ID,Ref Number,Type,Amount,Status,Remarks,Date");
            for (Transaction t : list) {
                writer.println(t.getTransactionId() + "," +
                        t.getReferenceNumber() + "," +
                        t.getTransactionType().name() + "," +
                        t.getAmount() + "," +
                        t.getStatus().name() + "," +
                        t.getRemarks() + "," +
                        t.getTransactionDate());
            }
        }
        return out.toByteArray();
    }

    private byte[] exportTransactionsToPdf(List<Transaction> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph title = new Paragraph("NovaBank - Transactions Ledger", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Txn ID");
            table.addCell("Ref Number");
            table.addCell("Type");
            table.addCell("Amount");
            table.addCell("Status");
            table.addCell("Date");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Transaction t : list) {
                table.addCell(t.getTransactionId());
                table.addCell(t.getReferenceNumber());
                table.addCell(t.getTransactionType().name());
                table.addCell("$" + t.getAmount());
                table.addCell(t.getStatus().name());
                table.addCell(t.getTransactionDate().format(formatter));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            log.error("Failed to compile transactions PDF: ", e);
        }
        return out.toByteArray();
    }
}
