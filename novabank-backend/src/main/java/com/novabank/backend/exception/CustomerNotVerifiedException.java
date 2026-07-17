package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when trying to create accounts or perform banking operations
 * for customer profiles that have not completed KYC compliance verification.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomerNotVerifiedException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public CustomerNotVerifiedException(String message) {
        super(message);
    }
}
