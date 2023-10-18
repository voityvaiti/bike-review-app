package com.myproject.bikereviewapp.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ErrorDetailsDto {

    private HttpStatus httpStatus;

    private LocalDateTime timestamp;

    private String message;

}
