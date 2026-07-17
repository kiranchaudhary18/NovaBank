package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an authenticated client has insufficient privileges to access a resource.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ForbiddenException with the specified detail message.
     *
     * @param message the detail message
     */
    public ForbiddenException(String message) {
        super(message);
    }

    /**
     * Constructs a new ForbiddenException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
