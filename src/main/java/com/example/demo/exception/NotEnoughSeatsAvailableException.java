package com.example.demo.exception;

public class NotEnoughSeatsAvailableException extends RuntimeException {
    public NotEnoughSeatsAvailableException(String message) {
        super(message);
    }
}
