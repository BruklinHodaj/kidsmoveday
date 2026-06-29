package com.einfachgesund.kidsmoveday.exception;

/**
 * Custom unchecked exception thrown when a user cannot be found.
 *
 * <p>Thrown by UserService and RegistrationService when a lookup
 * by ID or Versicherungsnummer returns no result.</p>
 *
 * <p>Covers: R10 (Exception Handling)</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
public class UserNotFoundException extends RuntimeException {

    /** The ID of the user that was not found, if applicable. */
    private final Long userId;

    /**
     * Constructs a UserNotFoundException with a custom message.
     * @param message human-readable error description
     */
    public UserNotFoundException(String message) {
        super(message);
        this.userId = null;
    }

    /**
     * Constructs a UserNotFoundException for a specific user ID.
     * @param id the ID of the user that was not found
     */
    public UserNotFoundException(Long id) {
        super("User with ID " + id + " was not found.");
        this.userId = id;
    }

    /**
     * Returns the ID of the missing user.
     * @return user ID or null if not applicable
     */
    public Long getUserId() {
        return userId;
    }
}