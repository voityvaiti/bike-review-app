package com.myproject.bikereviewapp.exceptionhandler;

import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserDuplicationException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_MESSAGE = "Oops! Something went wrong.";

    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundExceptions(
            RuntimeException exception, Model model
    ) {
        return handleException(exception, model, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserIsNotAuthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorizedExceptions(
            RuntimeException exception, Model model
    ) {
        return handleException(exception, model, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserDuplicationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestExceptions(
            RuntimeException exception, Model model
    ) {
        return handleException(exception, model, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(Model model) {

        model.addAttribute("errorDetails",
                new ErrorDetailsDto(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), DEFAULT_MESSAGE));
        return "error/error_page";
    }


    private String handleException(
            RuntimeException exception, Model model, HttpStatus httpStatus
    ) {
        model.addAttribute("errorDetails",
                new ErrorDetailsDto(httpStatus, LocalDateTime.now(), exception.getMessage()));
        return "error/error_page";
    }

}
