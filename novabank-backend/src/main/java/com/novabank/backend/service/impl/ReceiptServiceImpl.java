package com.novabank.backend.service.impl;

import com.novabank.backend.dto.TransactionReceiptResponse;
import com.novabank.backend.entity.Transaction;
import com.novabank.backend.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service implementation for generating transaction receipts.
 *
 * @author Senior Java Backend Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    @Override
    public TransactionReceiptResponse generateReceipt(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        String senderNum = (transaction.getSenderAccount() != null)
                ? transaction.getSenderAccount().getAccountNumber()
                : "CASH-DEPOT";

        String receiverNum = (transaction.getReceiverAccount() != null)
                ? transaction.getReceiverAccount().getAccountNumber()
                : "CASH-DEPOT";

        log.info("Generating receipt for transaction ID: {}", transaction.getTransactionId());

        return TransactionReceiptResponse.builder()
                .referenceNumber(transaction.getReferenceNumber())
                .transactionId(transaction.getTransactionId())
                .senderAccountNumber(senderNum)
                .receiverAccountNumber(receiverNum)
                .amount(transaction.getAmount())
                .charges(BigDecimal.ZERO) // Currently standard 0 transaction charge
                .transactionType(transaction.getTransactionType())
                .transactionDate(transaction.getTransactionDate())
                .status(transaction.getStatus())
                .remarks(transaction.getRemarks())
                .build();
    }
}
