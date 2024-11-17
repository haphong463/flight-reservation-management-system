package com.windev.user_service.exception;

public class AuthorityNotFoundException extends RuntimeException{
    public AuthorityNotFoundException(String message) {
        super(message);
    }
}
