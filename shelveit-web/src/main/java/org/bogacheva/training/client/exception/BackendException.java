package org.bogacheva.training.client.exception;

import lombok.Getter;
import org.bogacheva.training.client.dto.ShelveItError;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Exception thrown when backend API returns an error response.
 */
@Getter
public class BackendException extends RuntimeException {
    private final ShelveItError errorResponse;
    private final HttpStatus httpStatus;
    private final LocalDateTime timestamp;

    public BackendException(ShelveItError errorResponse, HttpStatus httpStatus) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
        this.httpStatus = resolveHttpStatus(httpStatus);
        this.timestamp = LocalDateTime.now();
    }

    private HttpStatus resolveHttpStatus(HttpStatus httpStatus) {
        return httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public int getStatusCode() {
        return httpStatus.value();
    }

    public String getErrorTitle() {
        return errorResponse != null ? errorResponse.getError() : "Error";
    }
}

