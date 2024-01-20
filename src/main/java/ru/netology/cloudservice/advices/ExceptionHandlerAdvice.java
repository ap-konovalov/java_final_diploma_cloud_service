package ru.netology.cloudservice.advices;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudservice.exceptions.BadCredentialsException;
import ru.netology.cloudservice.exceptions.FileStorageException;
import ru.netology.cloudservice.exceptions.NoSuchUserException;
import ru.netology.cloudservice.exceptions.UserAddException;
import ru.netology.cloudservice.models.ErrorResponseDto;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<ErrorResponseDto> noSuchUserErrorHandler(NoSuchUserException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), 1));
    }

    @ExceptionHandler(UserAddException.class)
    public ResponseEntity<ErrorResponseDto> userAddErrorHandler(UserAddException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), 2));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> badCredentialsErrorHandler(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getErrorResponseDto(exception.getMessage(), 3));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> constraintViolationErrorHandler(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), 4));
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponseDto> fileStorageErrorHandler(FileStorageException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), 5));
    }

    private ErrorResponseDto getErrorResponseDto(String errorMessage, int errorId) {
        return ErrorResponseDto.builder()
                               .message(errorMessage)
                               .id(errorId)
                               .build();
    }
}
