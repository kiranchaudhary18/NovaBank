package com.novabank.backend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response envelope to standardize all REST API responses across NovaBank.
 *
 * @param <T> the type of the data payload
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /** Indicates whether the request was processed successfully. */
    private boolean success;

    /** Descriptive message regarding the response state or errors. */
    private String message;

    /** The actual payload containing the requested resource or result. */
    private T data;

    /** Timestamp of when the response was generated. */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Helper constructor to create a response without custom timestamp (uses current time).
     *
     * @param success whether request was successful
     * @param message response message
     * @param data response payload
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
