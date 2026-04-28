package com.library.exception;

/**
 * Thrown when a business rule is violated (e.g., no available copies, duplicate ISBN).
 */
public class LibraryException extends RuntimeException {

    public LibraryException(String message) {
        super(message);
    }
}
