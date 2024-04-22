package com.lti.gatewayservice.exception;

public class AuthException extends RuntimeException{
    public AuthException(String message) {
        super(message);
    }
}
