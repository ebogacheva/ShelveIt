package org.bogacheva.training.contoller;


import org.bogacheva.training.client.dto.ErrorModel;
import org.bogacheva.training.client.exception.BackendException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

/**
 * Global exception handler for web controllers.
 * Handles BackendException and other exceptions, showing user-friendly error pages.
 */
@ControllerAdvice
public class WebControllerAdvice {

    @ExceptionHandler(BackendException.class)
    public ModelAndView handleBackendException(BackendException ex) {
        return createErrorModelAndView(ex.getHttpStatus(), ex.getMessage(), ex.getTimestamp());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex) {
        return createErrorModelAndView(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            LocalDateTime.now()
        );
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ModelAndView handleHttpClientError(HttpClientErrorException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        // Try to extract message from response body, fallback to status message
        String message = ex.getResponseBodyAsString();
        if (message == null || message.isEmpty() || message.trim().equals("{}")) {
            message = ex.getStatusCode().toString();
        }
        return createErrorModelAndView(status, message, LocalDateTime.now());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ModelAndView handleHttpServerError(HttpServerErrorException ex) {
        return createErrorModelAndView(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An internal server error occurred. Please try again later.",
            LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        return createErrorModelAndView(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error has occurred. Please try again later.",
            LocalDateTime.now()
        );
    }

    private ModelAndView createErrorModelAndView(HttpStatus status, 
                                                    String backendMessage,
                                                    LocalDateTime timestamp) {
        HttpStatus resolvedStatus = resolveHttpStatus(status);
        String title = getTitleForStatus(resolvedStatus);
        String details = getDetailsForStatus(resolvedStatus, backendMessage);
        
        ErrorModel errorModel = new ErrorModel(
            title,
            details,
            resolvedStatus.value(),
            timestamp
        );
        
        ModelAndView modelAndView = new ModelAndView("error", "error", errorModel);
        modelAndView.setStatus(resolvedStatus);
        return modelAndView;
    }

    private HttpStatus resolveHttpStatus(HttpStatus status) {
        return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String getTitleForStatus(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "Resource Not Found";
            case BAD_REQUEST -> "Invalid Request";
            case INTERNAL_SERVER_ERROR -> "Server Error";
            default -> "Error";
        };
    }

    private String getDetailsForStatus(HttpStatus status, String backendMessage) {
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return "An internal server error occurred. Please try again later.";
        }
        return backendMessage != null ? backendMessage : "An error occurred while processing your request.";
    }
}

