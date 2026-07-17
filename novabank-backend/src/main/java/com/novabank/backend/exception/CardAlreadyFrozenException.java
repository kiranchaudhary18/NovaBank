package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to freeze an already frozen card.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CardAlreadyFrozenException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public CardAlreadyFrozenException(String message) {
        super(message);
    }
}
