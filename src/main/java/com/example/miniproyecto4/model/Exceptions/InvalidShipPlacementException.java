package com.example.miniproyecto4.model.Exceptions;

public class InvalidShipPlacementException extends Exception {

    public InvalidShipPlacementException(String message) {
        super(message);
    }

    public InvalidShipPlacementException(String message, Throwable cause) {
        super(message, cause);
    }
}