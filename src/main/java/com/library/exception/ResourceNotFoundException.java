package com.library.exception;

/**
 * Thrown when a requested resource (book, user, record) cannot be found.
 * Maps to HTTP 404 via the global exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
}
