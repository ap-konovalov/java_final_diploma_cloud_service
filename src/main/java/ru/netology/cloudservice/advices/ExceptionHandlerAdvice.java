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
import ru.netology.cloudservice.dto.ErrorResponseDto;

import static ru.netology.cloudservice.enums.ErrorCode.BAD_CREDENTIALS_ERROR;
import static ru.netology.cloudservice.enums.ErrorCode.CONSTRAINT_VIOLATION_ERROR;
import static ru.netology.cloudservice.enums.ErrorCode.FILE_STORAGE_ERROR;
import static ru.netology.cloudservice.enums.ErrorCode.NO_SUCH_USER_ERROR;
import static ru.netology.cloudservice.enums.ErrorCode.USER_ADD_ERROR;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<ErrorResponseDto> noSuchUserErrorHandler(NoSuchUserException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), NO_SUCH_USER_ERROR.getCode()));
    }

    @ExceptionHandler(UserAddException.class)
    public ResponseEntity<ErrorResponseDto> userAddErrorHandler(UserAddException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), USER_ADD_ERROR.getCode()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> badCredentialsErrorHandler(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                getErrorResponseDto(exception.getMessage(), BAD_CREDENTIALS_ERROR.getCode()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> constraintViolationErrorHandler(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), CONSTRAINT_VIOLATION_ERROR.getCode()));
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponseDto> fileStorageErrorHandler(FileStorageException exception) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(exception.getMessage(), FILE_STORAGE_ERROR.getCode()));
    }

    private ErrorResponseDto getErrorResponseDto(String errorMessage, int errorId) {
        return ErrorResponseDto.builder()
                               .message(errorMessage)
                               .id(errorId)
                               .build();
    }
}
