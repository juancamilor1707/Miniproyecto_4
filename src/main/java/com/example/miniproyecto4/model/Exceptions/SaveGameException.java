package com.example.miniproyecto4.model.Exceptions;

public class SaveGameException extends Exception {

    public SaveGameException(String message) {
        super(message);
    }

    public SaveGameException(String message, Throwable cause) {
        super(message, cause);
    }
}