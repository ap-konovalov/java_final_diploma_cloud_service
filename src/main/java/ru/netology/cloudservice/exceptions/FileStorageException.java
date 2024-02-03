package ru.netology.cloudservice.exceptions;

public class FileStorageException extends IllegalArgumentException {

    public FileStorageException(String message) {
        super(message);
    }
}
