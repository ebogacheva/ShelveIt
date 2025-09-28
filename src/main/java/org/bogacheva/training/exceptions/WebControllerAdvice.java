package org.bogacheva.training.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global exception handler for web controllers, not REST API controllers.
 */
@ControllerAdvice(basePackages = "org.bogacheva.training.contoller.web")
@Slf4j
public class WebControllerAdvice {

    @ExceptionHandler(StorageNotFoundException.class)
    public ModelAndView handleStorageNotFound(StorageNotFoundException ex) {
        log.error("Storage not found: {}", ex.getMessage());
        return createErrorModelAndView(
                "Storage Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ModelAndView handleItemNotFound(ItemNotFoundException ex) {
        log.error("Item not found: {}", ex.getMessage());
        return createErrorModelAndView(
                "Item Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return createErrorModelAndView(
                "Resource Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
    }

    @ExceptionHandler(InvalidStorageHierarchyException.class)
    public ModelAndView handleInvalidStorageHierarchy(InvalidStorageHierarchyException ex) {
        log.error("Invalid storage hierarchy: {}", ex.getMessage());
        return createErrorModelAndView(
                "Invalid Storage Hierarchy",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(InvalidItemOperationException.class)
    public ModelAndView handleInvalidItemOperation(InvalidItemOperationException ex) {
        log.error("Invalid item operation: {}", ex.getMessage());
        return createErrorModelAndView(
                "Invalid Item Operation",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        return createErrorModelAndView(
                "Invalid Input",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleValidationError(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        String errorMessage = "Validation failed: " + String.join(", ", errors);
        log.error("Validation error: {}", errorMessage);
        
        return createErrorModelAndView(
                "Validation Error",
                errorMessage,
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return createErrorModelAndView(
                "Unexpected Error",
                "An internal server error has occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }


    private ModelAndView createErrorModelAndView(String title, String details, int statusCode) {
        ModelAndView modelAndView = new ModelAndView("error");
        
        ErrorModel errorModel = new ErrorModel(
                title,
                details,
                statusCode,
                LocalDateTime.now()
        );
        
        modelAndView.addObject("error", errorModel);
        return modelAndView;
    }
}
