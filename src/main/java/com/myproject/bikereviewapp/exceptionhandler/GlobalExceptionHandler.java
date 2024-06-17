package com.myproject.bikereviewapp.exceptionhandler;

import com.myproject.bikereviewapp.exceptionhandler.exception.CloudException;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UniquenessConstraintViolationException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_MESSAGE = "Oops! Something went wrong.";
    private static final String ERROR_DETAILS_ATTR = "errorDetails";
    private static final String ERROR_PAGE = "error/error_page";

    @ExceptionHandler({EntityNotFoundException.class, UniquenessConstraintViolationException.class})
    public String handleBadRequestExceptions(
            RuntimeException exception, Model model, HttpServletResponse response
    ) {
        return handleException(exception, model, HttpStatus.BAD_REQUEST, response);
    }

    @ExceptionHandler({UserIsNotAuthorizedException.class})
    public String handleUserIsNotAuthorizedException(
            RuntimeException exception, Model model, HttpServletResponse response
    ) {
        return handleException(exception, model, HttpStatus.UNAUTHORIZED, response);
    }

    @ExceptionHandler(CloudException.class)
    public String handleCloudUploadingException(CloudException ex, Model model, HttpServletResponse response) {

        return handleException(ex, model, HttpStatus.INTERNAL_SERVER_ERROR, response);
    }

    @ExceptionHandler({RuntimeException.class})
    public String handleRuntimeException(RuntimeException e, Model model, HttpServletResponse response) {

        e.printStackTrace();

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        response.setStatus(httpStatus.value());

        model.addAttribute(ERROR_DETAILS_ATTR,
                new ErrorDetailsDto(httpStatus, LocalDateTime.now(), DEFAULT_MESSAGE));
        return ERROR_PAGE;
    }


    private String handleException(
            RuntimeException exception, Model model, HttpStatus httpStatus, HttpServletResponse response
    ) {
        response.setStatus(httpStatus.value());

        model.addAttribute(ERROR_DETAILS_ATTR,
                new ErrorDetailsDto(httpStatus, LocalDateTime.now(), exception.getMessage()));
        return ERROR_PAGE;
    }

}
