package com.windev.flight_service.exception;

public class SeatNotBelongToFlightException extends RuntimeException{
    public SeatNotBelongToFlightException(String message){
        super(message);
    }
}
