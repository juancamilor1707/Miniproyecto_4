package com.example.miniproyecto4.model.Exceptions;

public class LoadGameException extends Exception {

    public LoadGameException(String message) {
        super(message);
    }

    public LoadGameException(String message, Throwable cause) {
        super(message, cause);
    }
}