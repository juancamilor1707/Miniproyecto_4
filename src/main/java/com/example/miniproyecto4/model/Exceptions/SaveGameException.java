package com.example.miniproyecto4.model.Exceptions;

/**
 * Exception thrown when a game cannot be saved.
 * This typically occurs due to file system errors or serialization issues.
 */
public class SaveGameException extends Exception {

    /**
     * Constructs a new SaveGameException with the specified detail message.
     *
     * @param message The detail message
     */
    public SaveGameException(String message) {
        super(message);
    }

    /**
     * Constructs a new SaveGameException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of this exception
     */
    public SaveGameException(String message, Throwable cause) {
        super(message, cause);
    }
}