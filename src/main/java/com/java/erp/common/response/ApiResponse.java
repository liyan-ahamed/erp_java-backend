package com.java.erp.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized API response wrapper for all endpoints.
 * Provides a consistent response structure across the application.
 *
 * @param <T> the type of response data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;

    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Create a successful response with data.
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = "Operation successful";
        response.data = data;
        return response;
    }

    /**
     * Create a successful response with data and a custom message.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        return response;
    }

    /**
     * Create a successful response with only a message (no data).
     */
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        return response;
    }

    /**
     * Create an error response with a single message.
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        return response;
    }

    /**
     * Create an error response with multiple error details.
     */
    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.errors = errors;
        return response;
    }

    // Getters

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
