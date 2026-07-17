package com.novabank.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client request contains bad input, invalid arguments,
 * or represents an invalid business state action.
 *
 * @author Senior Java Backend Architect
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new BadRequestException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
