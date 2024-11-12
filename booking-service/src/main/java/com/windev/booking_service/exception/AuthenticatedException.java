package com.windev.booking_service.exception;

public class AuthenticatedException extends RuntimeException{
    public AuthenticatedException(String message) {
        super(message);
    }
}
