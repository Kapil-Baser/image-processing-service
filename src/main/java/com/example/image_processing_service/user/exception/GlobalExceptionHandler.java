package com.example.image_processing_service.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = "timestamp";

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());

        problemDetail.setTitle("Username already exists");
        problemDetail.setProperty(TIMESTAMP, Instant.now());

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "The request payload contains missing or invalid fields.");

        Map<String, String> validationErrors = new HashMap<>();

        ex.getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            validationErrors.put(field, errorMessage);
        });

        problemDetail.setTitle("Validation failed");
        problemDetail.setProperty("errors", validationErrors);
        problemDetail.setProperty(TIMESTAMP, Instant.now());

        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ProblemDetail> handleJwtValidationException(JwtValidationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());

        problemDetail.setTitle("Provided JWT is invalid.");
        problemDetail.setProperty(TIMESTAMP, Instant.now());

        return ResponseEntity.of(problemDetail).build();
    }
}
