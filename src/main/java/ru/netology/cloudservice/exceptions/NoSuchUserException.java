package ru.netology.cloudservice.exceptions;

public class NoSuchUserException extends IllegalArgumentException {

    public NoSuchUserException(String message) {
        super(message);
    }
}
