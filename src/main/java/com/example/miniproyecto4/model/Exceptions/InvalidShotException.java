package com.example.miniproyecto4.model.Exceptions;

public class InvalidShotException extends Exception {

    public InvalidShotException(String message) {
        super(message);
    }

    public InvalidShotException(String message, Throwable cause) {
        super(message, cause);
    }
}