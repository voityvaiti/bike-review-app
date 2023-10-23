package com.myproject.bikereviewapp.exceptionhandler.exception;

public class UserDuplicationException extends RuntimeException {

    public UserDuplicationException(String message) {
        super(message);
    }
}
