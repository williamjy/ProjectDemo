package com.nuggets.valueeats.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<String> handleInvalidInput(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Could not be authorised");
    }
}
