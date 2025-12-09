package com.example.miniproyecto4.model.Exceptions;

/**
 * Exception thrown when a ship placement is invalid.
 * This includes placements outside board bounds or overlapping with existing ships.
 */
public class InvalidShipPlacementException extends Exception {

    /**
     * Constructs a new InvalidShipPlacementException with the specified detail message.
     *
     * @param message The detail message
     */
    public InvalidShipPlacementException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidShipPlacementException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of this exception
     */
    public InvalidShipPlacementException(String message, Throwable cause) {
        super(message, cause);
    }
}