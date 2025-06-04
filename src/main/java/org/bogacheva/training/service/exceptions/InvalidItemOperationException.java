package org.bogacheva.training.service.exceptions;

public class InvalidItemOperationException extends RuntimeException {
    public InvalidItemOperationException(String message) {
        super(message);
    }
}
