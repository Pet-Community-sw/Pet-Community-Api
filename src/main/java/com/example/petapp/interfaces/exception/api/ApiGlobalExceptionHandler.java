package com.example.petapp.interfaces.exception.api;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiGlobalExceptionHandler {
    @ExceptionHandler(PetCommunityException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(PetCommunityException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.error("{}: {}", errorCode.getCode(), ex.getMessage());
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiErrorResponse.of(errorCode.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("{}: {}", ErrorCode.BAD_REQUEST.getCode(), ex.getMessage());
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getStatus())
                .body(ApiErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // JSON(@RequestBody)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return buildValidationErrorResponse(ex.getBindingResult());
    }

    @ExceptionHandler(BindException.class) // multipart/form-data(@ModelAttribute)
    public ResponseEntity<ApiErrorResponse> handleBindException(BindException ex) {
        return buildValidationErrorResponse(ex.getBindingResult());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        log.error(ErrorCode.INTERNAL_ERROR.getCode(), ex);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(ApiErrorResponse.of(ErrorCode.INTERNAL_ERROR.getCode(), "서버 내부 오류가 발생했습니다."));
    }

    private ResponseEntity<ApiErrorResponse> buildValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (a, b) -> a // 중복 필드 첫 메시지 유지
                ));
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(ApiErrorResponse.validation(errors));
    }
}
