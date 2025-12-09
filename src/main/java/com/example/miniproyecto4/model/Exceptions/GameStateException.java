package com.example.miniproyecto4.model.Exceptions;

/**
 * Runtime exception thrown when an invalid game state operation is attempted.
 * This is an unchecked exception indicating a programming error in game state management.
 */
public class GameStateException extends RuntimeException {

    /**
     * Constructs a new GameStateException with the specified detail message.
     *
     * @param message The detail message
     */
    public GameStateException(String message) {
        super(message);
    }

    /**
     * Constructs a new GameStateException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of this exception
     */
    public GameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}