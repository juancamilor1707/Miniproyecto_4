package com.example.miniproyecto4.model.Exceptions;

/**
 * Exception thrown when a game cannot be loaded from saved data.
 * This is a checked exception that must be handled when loading games.
 */
public class LoadGameException extends Exception {

    /**
     * Constructs a new LoadGameException with the specified detail message.
     *
     * @param message The detail message
     */
    public LoadGameException(String message) {
        super(message);
    }

    /**
     * Constructs a new LoadGameException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of this exception
     */
    public LoadGameException(String message, Throwable cause) {
        super(message, cause);
    }
}