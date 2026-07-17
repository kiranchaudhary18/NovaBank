package com.novabank.backend.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.novabank.backend.dto.StatementResponse;
import com.novabank.backend.dto.StatementSummary;
import com.novabank.backend.dto.TransactionHistoryResponse;
import com.novabank.backend.service.PdfStatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service implementation managing PDF Statement generations.
 * Uses OpenPDF to build tables, grids, and headings.
 *
 * @author Senior Java Backend Architect
 */
@Service
@Slf4j
public class PdfStatementServiceImpl implements PdfStatementService {

    @Override
    public byte[] generatePdfStatement(StatementResponse response) {
        log.info("Generating PDF Statement for account: {}", response.getSummary().getAccountNumber());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Setup professional corporate fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(0, 51, 102)); // Navy Blue
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 51, 102));
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            Font boldTextFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

            // Document Header
            Paragraph title = new Paragraph("NOVABANK STATEMENT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            StatementSummary summary = response.getSummary();

            // Client & Period Information Grid
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(15);

            infoTable.addCell(createNoBorderCell("Customer Name: " + summary.getCustomerName(), boldTextFont));
            infoTable.addCell(createNoBorderCell("Statement Period: " + summary.getPeriod(), boldTextFont));
            infoTable.addCell(createNoBorderCell("Account Number: " + summary.getAccountNumber(), boldTextFont));
            infoTable.addCell(createNoBorderCell("Generated On: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), textFont));

            document.add(infoTable);

            // Balance Summary Metrics
            document.add(new Paragraph("ACCOUNT SUMMARY", sectionFont));
            PdfPTable summaryTable = new PdfPTable(4);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(8);
            summaryTable.setSpacingAfter(20);

            addSummaryHeader(summaryTable, "Opening Balance", headerFont);
            addSummaryHeader(summaryTable, "Total Credits", headerFont);
            addSummaryHeader(summaryTable, "Total Debits", headerFont);
            addSummaryHeader(summaryTable, "Closing Balance", headerFont);

            addSummaryCell(summaryTable, "$" + summary.getOpeningBalance(), textFont);
            addSummaryCell(summaryTable, "$" + summary.getTotalCredits(), textFont);
            addSummaryCell(summaryTable, "$" + summary.getTotalDebits(), textFont);
            addSummaryCell(summaryTable, "$" + summary.getClosingBalance(), textFont);

            document.add(summaryTable);

            // Transaction Ledger Table
            document.add(new Paragraph("TRANSACTION LEDGER", sectionFont));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(8);
            table.setWidths(new float[]{2.5f, 4f, 2f, 2f, 2f, 3.5f}); // Column proportional sizes

            // Table Headers
            addLedgerHeader(table, "Date", headerFont);
            addLedgerHeader(table, "Reference / Transaction ID", headerFont);
            addLedgerHeader(table, "Debit (-)", headerFont);
            addLedgerHeader(table, "Credit (+)", headerFont);
            addLedgerHeader(table, "Running Balance", headerFont);
            addLedgerHeader(table, "Remarks", headerFont);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            boolean alternate = false;

            for (TransactionHistoryResponse txn : response.getTransactions()) {
                Color rowColor = alternate ? new Color(245, 245, 245) : Color.WHITE;
                alternate = !alternate;

                table.addCell(createLedgerCell(txn.getTransactionDate().format(formatter), textFont, rowColor));
                table.addCell(createLedgerCell(txn.getReferenceNumber() + "\n(" + txn.getTransactionId() + ")", textFont, rowColor));

                String debitStr = txn.getDebitAmount() != null ? "$" + txn.getDebitAmount() : "-";
                table.addCell(createLedgerCell(debitStr, textFont, rowColor));

                String creditStr = txn.getCreditAmount() != null ? "$" + txn.getCreditAmount() : "-";
                table.addCell(createLedgerCell(creditStr, textFont, rowColor));

                table.addCell(createLedgerCell("$" + txn.getRunningBalance(), textFont, rowColor));
                table.addCell(createLedgerCell(txn.getRemarks(), textFont, rowColor));
            }

            document.add(table);
            document.close();
            log.info("PDF Statement generated successfully for account: {}", summary.getAccountNumber());
        } catch (DocumentException exception) {
            log.error("OpenPDF document building failed: ", exception);
        }

        return out.toByteArray();
    }

    private PdfPCell createNoBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        return cell;
    }

    private void addSummaryHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(0, 51, 102));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addSummaryCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addLedgerHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(0, 51, 102));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private PdfPCell createLedgerCell(String text, Font font, Color backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
