package org.bogacheva.training.service.exceptions;

public class InvalidStorageHierarchyException extends RuntimeException {
  public InvalidStorageHierarchyException(String message) {
    super(message);
  }
}
