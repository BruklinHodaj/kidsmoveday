package com.einfachgesund.kidsmoveday.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers.
 *
 * <p>Catches exceptions thrown anywhere in the application and
 * converts them into structured JSON error responses with
 * appropriate HTTP status codes.</p>
 *
 * <p>Covers: R10 (Exception Handling)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles UserNotFoundException — returns 404 Not Found.
     * @param ex the exception thrown
     * @return structured error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UserNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 404);
        error.put("error", "User Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles RegistrationException — returns 400 Bad Request.
     * @param ex the exception thrown
     * @return structured error response with error code
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Map<String, Object>> handleRegistration(
            RegistrationException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", ex.getErrorCode());
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles all other unexpected exceptions — returns 500 Internal Server Error.
     * @param ex the exception thrown
     * @return structured error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}