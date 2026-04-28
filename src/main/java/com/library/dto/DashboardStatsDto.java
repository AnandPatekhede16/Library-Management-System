package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Lightweight stats DTO sent to the admin dashboard.
 */
@Data
@AllArgsConstructor
public class DashboardStatsDto {

    private long totalBooks;
    private long totalUsers;
    private long totalBorrowed;     // currently borrowed
    private long totalOverdue;      // overdue but not returned
    private long totalCategories;
}
