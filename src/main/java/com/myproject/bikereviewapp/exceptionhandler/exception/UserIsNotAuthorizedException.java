package com.myproject.bikereviewapp.exceptionhandler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserIsNotAuthorizedException extends RuntimeException {

    public UserIsNotAuthorizedException(String message) {
        super(message);
    }
}
