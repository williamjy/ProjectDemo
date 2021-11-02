package com.nuggets.valueeats.controller.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String string) {
        super(string);
    }
}
