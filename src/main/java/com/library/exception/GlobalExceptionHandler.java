package com.library.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that catches unhandled exceptions
 * and renders a user-friendly Thymeleaf error page.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle resource-not-found: redirect to a 404-style error page.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    /**
     * Handle business logic violations.
     */
    @ExceptionHandler(LibraryException.class)
    public String handleLibraryException(LibraryException ex, Model model) {
        model.addAttribute("errorTitle", "Operation Failed");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    /**
     * Catch-all for unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "error/error";
    }
}
