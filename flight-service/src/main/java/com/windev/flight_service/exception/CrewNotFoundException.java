package com.windev.flight_service.exception;

public class CrewNotFoundException extends RuntimeException{
    public CrewNotFoundException(String message) {
        super(message);
    }
}
