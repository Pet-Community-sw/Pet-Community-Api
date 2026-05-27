package com.example.petapp.application.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND"),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");

    private final HttpStatus status;
    private final String code;
}
