package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an operational request conflicts with existing database records
 * (e.g., trying to register an email address that is already active).
 * Maps to HTTP Status 409 Conflict.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ConflictException.
     *
     * @param message error details message
     */
    public ConflictException(String message) {
        super(message);
    }
}
