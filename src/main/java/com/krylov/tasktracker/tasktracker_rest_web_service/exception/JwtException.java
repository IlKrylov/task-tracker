package com.krylov.tasktracker.tasktracker_rest_web_service.exception;

public class JwtException extends RuntimeException {

    public JwtException() {
        super();
    }

    public JwtException(String message) {
        super(message);
    }
}
