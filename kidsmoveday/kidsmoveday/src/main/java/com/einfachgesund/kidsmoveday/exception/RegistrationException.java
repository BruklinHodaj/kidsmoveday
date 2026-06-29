package com.einfachgesund.kidsmoveday.exception;

/**
 * Custom unchecked exception for registration-related errors.
 *
 * <p>Thrown when a registration attempt fails due to business rule
 * violations such as a full event, duplicate registration, or
 * invalid guest data.</p>
 *
 * <p>Covers: R10 (Exception Handling)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
public class RegistrationException extends RuntimeException {

    /** Machine-readable error code for API responses. */
    private final String errorCode;

    /**
     * Constructs a RegistrationException with a message.
     * @param message human-readable error description
     */
    public RegistrationException(String message) {
        super(message);
        this.errorCode = "REGISTRATION_ERROR";
    }

    /**
     * Constructs a RegistrationException with a message and error code.
     * @param message   human-readable error description
     * @param errorCode machine-readable code (e.g. EVENT_FULL)
     */
    public RegistrationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Returns the machine-readable error code.
     * @return error code string
     */
    public String getErrorCode() {
        return errorCode;
    }
}