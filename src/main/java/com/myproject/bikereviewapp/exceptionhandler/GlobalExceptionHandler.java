package com.myproject.bikereviewapp.exceptionhandler;

import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(
            EntityNotFoundException exception, Model model
    ) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/entity_not_found";
    }

}
