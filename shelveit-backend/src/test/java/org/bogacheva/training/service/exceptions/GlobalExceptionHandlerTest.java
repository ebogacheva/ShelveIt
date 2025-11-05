package org.bogacheva.training.service.exceptions;

import org.bogacheva.training.exceptions.GlobalExceptionHandler;
import org.bogacheva.training.exceptions.InvalidStorageHierarchyException;
import org.bogacheva.training.exceptions.ShelveItError;
import org.bogacheva.training.exceptions.StorageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleStorageNotFoundException_shouldReturnNotFound() {
        StorageNotFoundException ex = new StorageNotFoundException(123L);

        ResponseEntity<ShelveItError> response = handler.handle(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("123");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ShelveItError> response = handler.handle(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Invalid argument");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void handleInvalidStorageHierarchyException_shouldReturnBadRequest() {
        InvalidStorageHierarchyException ex = new InvalidStorageHierarchyException("Invalid hierarchy");

        ResponseEntity<ShelveItError> response = handler.handle(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Invalid hierarchy");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ShelveItError> response = handler.handle(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("An unexpected error occurred");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }
}

