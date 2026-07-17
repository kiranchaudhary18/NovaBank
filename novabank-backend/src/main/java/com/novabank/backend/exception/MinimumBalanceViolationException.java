package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a debit transaction would leave the account balance below the required bank minimum.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MinimumBalanceViolationException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public MinimumBalanceViolationException(String message) {
        super(message);
    }
}
