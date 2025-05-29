package com.nextmove.auth_service.exception;

public class ExistentUserException extends RuntimeException {
    public ExistentUserException(String message) {
        super(message);
    }
}
