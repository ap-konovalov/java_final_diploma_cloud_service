package ru.netology.cloudservice.exceptions;

public class BadCredentialsException extends IllegalArgumentException {

    public BadCredentialsException(String message) {
        super(message);
    }
}
