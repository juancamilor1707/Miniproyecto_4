package com.example.miniproyecto4.model.Exceptions;

/**
 * Exception thrown when an invalid shot is attempted.
 * This includes shots at invalid coordinates or previously shot locations.
 */
public class InvalidShotException extends Exception {

    /**
     * Constructs a new InvalidShotException with the specified detail message.
     *
     * @param message The detail message
     */
    public InvalidShotException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidShotException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of this exception
     */
    public InvalidShotException(String message, Throwable cause) {
        super(message, cause);
    }
}
