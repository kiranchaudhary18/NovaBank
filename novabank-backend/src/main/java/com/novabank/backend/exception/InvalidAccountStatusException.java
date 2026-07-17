package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting operations (like status changes or transfers)
 * on accounts in invalid states (such as FROZEN or CLOSED).
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAccountStatusException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public InvalidAccountStatusException(String message) {
        super(message);
    }
}
