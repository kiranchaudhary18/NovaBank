package com.novabank.backend.util;

import com.novabank.backend.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class to simplify generating unified ResponseEntity wrappers.
 *
 * @author Senior Java Backend Architect
 */
public final class ResponseUtil {

    private ResponseUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Generates a 200 OK successful response containing custom message and payload.
     *
     * @param message custom status description
     * @param data response payload
     * @param <T> data type
     * @return response entity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.ok(response);
    }

    /**
     * Generates a 200 OK successful response containing only a payload.
     *
     * @param data response payload
     * @param <T> data type
     * @return response entity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success("Request processed successfully", data);
    }

    /**
     * Generates a 201 Created successful response containing message and payload.
     *
     * @param message custom status description
     * @param data response payload
     * @param <T> data type
     * @return response entity
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Generates an error response with appropriate HTTP status and message.
     *
     * @param status HTTP status code mapping
     * @param message error description
     * @param <T> response type (usually Void)
     * @return response entity containing error envelope
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null);
        return ResponseEntity.status(status).body(response);
    }
}
