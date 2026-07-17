package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a customer tries to save a beneficiary account number
 * that is already registered on their profile list.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateBeneficiaryException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public DuplicateBeneficiaryException(String message) {
        super(message);
    }
}
