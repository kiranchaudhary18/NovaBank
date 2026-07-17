package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transaction exceeds single or daily limits configuration metrics.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransferLimitExceededException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public TransferLimitExceededException(String message) {
        super(message);
    }
}
