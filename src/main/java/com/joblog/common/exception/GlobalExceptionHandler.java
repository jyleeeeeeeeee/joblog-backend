package com.joblog.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 중복된 이메일 예외 처리
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
        String message = e.getMessage();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("DUPLICATE_EMAIL", message));
    }

    // @Valid 실패 시 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of("VALIDATION_ERROR", message));
    }

    // @Valid 실패 시 처리
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        String message = e.getMessage();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("UNAUTHORIZED", message));
    }

    // @Valid 실패 시 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(AccessDeniedException e) {
        String message = e.getMessage();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of("FORBIDDEN", message));
    }



    // 그 외 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        e.printStackTrace();
        String message = e.getMessage();

        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of("INTERNAL_SERVER_ERROR", message));
    }

    @ExceptionHandler(JwtInvalid401Exception.class)
    public ResponseEntity<ErrorResponse> handleJwt401Invalid(JwtInvalid401Exception e) {
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("UNAUTHORIZED", message));
    }

    @ExceptionHandler(JwtInvalid403Exception.class)
    public ResponseEntity<ErrorResponse> handleJwt403Invalid(JwtInvalid401Exception e) {
        String message = e.getMessage();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of("FORBIDDEN", message));
    }

}
