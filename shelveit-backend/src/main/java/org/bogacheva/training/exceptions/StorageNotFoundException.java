package org.bogacheva.training.exceptions;

public class StorageNotFoundException extends ResourceNotFoundException {
    public StorageNotFoundException(Long id) {
        super(String.format("Storage with ID: %s was not found.", id));
    }
}
