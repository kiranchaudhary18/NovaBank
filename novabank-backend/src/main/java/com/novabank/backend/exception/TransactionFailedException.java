package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transaction fails to process successfully due to general business errors.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionFailedException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public TransactionFailedException(String message) {
        super(message);
    }
}
