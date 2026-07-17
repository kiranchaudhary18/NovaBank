package com.novabank.backend.service.impl;

import com.novabank.backend.dto.StatementResponse;
import com.novabank.backend.dto.StatementSummary;
import com.novabank.backend.dto.TransactionHistoryResponse;
import com.novabank.backend.service.CsvStatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Service implementation managing CSV Statement serializations.
 * Formats data into a structured tabular text format.
 *
 * @author Senior Java Backend Architect
 */
@Service
@Slf4j
public class CsvStatementServiceImpl implements CsvStatementService {

    @Override
    public byte[] generateCsvStatement(StatementResponse response) {
        log.info("Generating CSV Statement for account: {}", response.getSummary().getAccountNumber());
        StatementSummary summary = response.getSummary();
        StringBuilder csv = new StringBuilder();

        // 1. Account Metadata Header Block
        csv.append("NovaBank Account Statement\n");
        csv.append("Account Number,").append(escapeCsv(summary.getAccountNumber())).append("\n");
        csv.append("Customer Name,").append(escapeCsv(summary.getCustomerName())).append("\n");
        csv.append("Statement Period,").append(escapeCsv(summary.getPeriod())).append("\n");
        csv.append("\n");

        // 2. Summary Metrics Block
        csv.append("Opening Balance,Total Credits,Total Debits,Closing Balance\n");
        csv.append(summary.getOpeningBalance()).append(",")
                .append(summary.getTotalCredits()).append(",")
                .append(summary.getTotalDebits()).append(",")
                .append(summary.getClosingBalance()).append("\n");
        csv.append("\n");

        // 3. Detailed Transactions Ledger
        csv.append("Date,Transaction ID,Reference Number,Type,Debit,Credit,Running Balance,Remarks\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (TransactionHistoryResponse txn : response.getTransactions()) {
            String dateStr = txn.getTransactionDate().format(formatter);
            String debitStr = txn.getDebitAmount() != null ? txn.getDebitAmount().toString() : "";
            String creditStr = txn.getCreditAmount() != null ? txn.getCreditAmount().toString() : "";

            csv.append(escapeCsv(dateStr)).append(",")
                    .append(escapeCsv(txn.getTransactionId())).append(",")
                    .append(escapeCsv(txn.getReferenceNumber())).append(",")
                    .append(escapeCsv(txn.getTransactionType().name())).append(",")
                    .append(debitStr).append(",")
                    .append(creditStr).append(",")
                    .append(txn.getRunningBalance()).append(",")
                    .append(escapeCsv(txn.getRemarks())).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String val) {
        if (val == null) {
            return "";
        }
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
