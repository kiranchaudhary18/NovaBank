package com.novabank.backend.service;

import com.novabank.backend.dto.TransactionReceiptResponse;
import com.novabank.backend.dto.TransferRequest;
import com.novabank.backend.entity.User;

/**
 * Service interface defining operations related to Account-to-Account fund transfers.
 *
 * @author Senior Java Backend Architect
 */
public interface TransferService {

    /**
     * Executes a fund transfer transaction from a sender account to a receiver account.
     * Enforces limits, balances verification, dynamic status checks, and is atomic.
     *
     * @param user authenticated customer initiator
     * @param request transfer parameters
     * @return TransactionReceiptResponse detailing execution results
     */
    TransactionReceiptResponse transferFunds(User user, TransferRequest request);
}
