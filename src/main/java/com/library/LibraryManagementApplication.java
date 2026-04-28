package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Library Management System application.
 * Enables scheduling (overdue reminders) and async processing.
 */
@SpringBootApplication
@EnableScheduling   // Required for @Scheduled tasks
@EnableAsync        // Required for @Async methods
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }
}
