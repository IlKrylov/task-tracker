package com.krylov.tasktracker.tasktracker_rest_web_service.exception;

public class InvalidDtoException extends RuntimeException {

    public InvalidDtoException() {
        super();
    }

    public InvalidDtoException(String message) {
        super(message);
    }
}
