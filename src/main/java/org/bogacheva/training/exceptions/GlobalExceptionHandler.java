package org.bogacheva.training.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(StorageNotFoundException.class)
    public ResponseEntity<ShelveItError> handle(StorageNotFoundException ex) {
        log.error("Storage not found: {}", ex.getMessage());
        return buildShelveItErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getClass().getName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ShelveItError> handle(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        return buildShelveItErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getClass().getName());
    }

    @ExceptionHandler(InvalidStorageHierarchyException.class)
    public ResponseEntity<ShelveItError> handle(InvalidStorageHierarchyException ex) {
        log.error("Invalid storage hierarchy: {}", ex.getMessage());
        return buildShelveItErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getClass().getName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ShelveItError> handle(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        String errorMessage = "Validation failed: " + String.join(", ", errors);
        log.error(errorMessage);
        return buildShelveItErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, ex.getClass().getName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ShelveItError> handle(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return buildShelveItErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                ex.getClass().getName()
        );
    }

    private ResponseEntity<ShelveItError> buildShelveItErrorResponse(HttpStatus status, String message, String exClassName) {
        ShelveItError shelveItError = new ShelveItError(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now(),
                exClassName
        );
        return new ResponseEntity<>(shelveItError, status);
    }
}







