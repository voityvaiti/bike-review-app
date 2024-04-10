package com.myproject.bikereviewapp.exceptionhandler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UniquenessConstraintViolationException extends RuntimeException {

    public UniquenessConstraintViolationException(String message) {
        super(message);
    }
}
