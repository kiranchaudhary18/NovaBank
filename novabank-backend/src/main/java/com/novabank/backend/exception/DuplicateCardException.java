package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to issue duplicate card types for the same account.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateCardException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public DuplicateCardException(String message) {
        super(message);
    }
}
