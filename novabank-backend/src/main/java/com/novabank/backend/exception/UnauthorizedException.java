package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when authentication credentials are missing, invalid, or expired.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new UnauthorizedException with the specified detail message.
     *
     * @param message the detail message
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnauthorizedException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
