package com.myproject.bikereviewapp.exceptionhandler.exception;

public class UniquenessConstraintViolationException extends RuntimeException {

    public UniquenessConstraintViolationException(String message) {
        super(message);
    }
}
