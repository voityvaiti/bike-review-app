package com.myproject.bikereviewapp.exceptionhandler;

import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class, UserIsNotAuthorizedException.class})
    public String handleEntityNotFoundException(
            RuntimeException exception, Model model
    ) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/error_page";
    }

}
