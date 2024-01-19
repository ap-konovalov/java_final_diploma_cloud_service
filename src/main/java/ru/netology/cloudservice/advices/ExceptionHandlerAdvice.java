package ru.netology.cloudservice.advices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudservice.exceptions.NoSuchUserException;
import ru.netology.cloudservice.exceptions.UserAddException;
import ru.netology.cloudservice.models.ErrorResponseDto;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<ErrorResponseDto> noSuchUserErrorHandler(NoSuchUserException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), 3));
    }

    @ExceptionHandler(UserAddException.class)
    public ResponseEntity<ErrorResponseDto> userAddErrorHandler(UserAddException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), 4));
    }

    private ErrorResponseDto getErrorResponseDto(String errorMessage, int errorId) {
        return ErrorResponseDto.builder()
                .message(errorMessage)
                .id(errorId)
                .build();
    }
}
