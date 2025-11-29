package com.example.mecManager.Common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard API Response wrapper for all endpoints
 * Provides type-safe generic response handling
 * 
 * @param <T> Data type in response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * Success flag (true/false instead of HTTP codes in body)
     */
    private Boolean success;
    
    /**
     * HTTP status code (for reference only, actual status in header)
     */
    private Integer code;
    
    /**
     * Response message (success or error description)
     */
    private String message;
    
    /**
     * Response data (generic type)
     */
    private T data;
    
    /**
     * Timestamp of response
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Error details list (for validation errors)
     */
    private List<String> errors;
    
    /**
     * Create a success response with data
     * 
     * @param data Response data
     * @param <T> Data type
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message("Success")
                .data(data)
                .build();
    }
    
    /**
     * Create a success response with data and custom message
     * 
     * @param message Custom message
     * @param data Response data
     * @param <T> Data type
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Create a success response without data
     * 
     * @param message Message
     * @param <T> Data type
     * @return ApiResponse with success=true and no data
     */
    public static <T> ApiResponse<T> successMessage(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message(message)
                .build();
    }
    
    /**
     * Create an error response
     * 
     * @param code HTTP status code
     * @param message Error message
     * @param <T> Data type
     * @return ApiResponse with success=false
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }
    
    /**
     * Create a validation error response with error list
     * 
     * @param code HTTP status code
     * @param message Error message
     * @param errors List of validation errors
     * @param <T> Data type
     * @return ApiResponse with error list
     */
    public static <T> ApiResponse<T> error(Integer code, String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }
}
