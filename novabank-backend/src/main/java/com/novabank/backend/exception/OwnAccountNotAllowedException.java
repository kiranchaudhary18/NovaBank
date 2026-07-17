package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a customer attempts to add one of their own bank accounts
 * as a saved money transfer beneficiary.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OwnAccountNotAllowedException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public OwnAccountNotAllowedException(String message) {
        super(message);
    }
}
