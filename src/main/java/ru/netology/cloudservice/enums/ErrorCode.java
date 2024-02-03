package ru.netology.cloudservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NO_SUCH_USER_ERROR(1),
    USER_ADD_ERROR(2),
    BAD_CREDENTIALS_ERROR(3),
    CONSTRAINT_VIOLATION_ERROR(4),
    FILE_STORAGE_ERROR(5);

    private final int code;
}
