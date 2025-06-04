package org.bogacheva.training.service.exceptions;

public class ItemNotFoundException extends ResourceNotFoundException {
    public ItemNotFoundException(Long id) {
        super(String.format("Item with ID: %s was not found.", id));
    }
}

