package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when operations are performed on expired cards.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CardExpiredException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public CardExpiredException(String message) {
        super(message);
    }
}
