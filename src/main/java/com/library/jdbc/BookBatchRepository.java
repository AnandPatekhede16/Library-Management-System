package com.library.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Demonstrates raw JDBC PreparedStatement batch operations using Spring's JdbcTemplate.
 *
 * Use case: bulk-insert book records (e.g., from a CSV import).
 * This bypasses Hibernate and executes raw SQL for maximum performance.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class BookBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Bulk-inserts a list of books using a JDBC PreparedStatement batch.
     * Each element in the list is an Object array:
     *   [title, author, isbn, total_copies, available_copies, category_id]
     *
     * @param books list of book data arrays
     */
    public void batchInsertBooks(List<Object[]> books) {
        String sql = """
            INSERT INTO books (title, author, isbn, total_copies, available_copies,
                               description, published_year, category_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        int[] updateCounts = jdbcTemplate.batchUpdate(sql, books);

        log.info("Batch-inserted {} book record(s) via raw JDBC PreparedStatement.",
                 updateCounts.length);
    }

    /**
     * Example: bulk-update overdue records to status='OVERDUE' using raw SQL batch.
     * Called externally; Hibernate-unaware operation.
     *
     * @param recordIds list of borrow_record IDs to mark overdue
     */
    public void batchMarkOverdue(List<Long> recordIds) {
        String sql = "UPDATE borrow_records SET status = 'OVERDUE' WHERE id = ?";

        List<Object[]> args = recordIds.stream()
            .map(id -> new Object[]{id})
            .toList();

        int[] counts = jdbcTemplate.batchUpdate(sql, args);
        log.info("Batch-marked {} record(s) as OVERDUE via raw JDBC.", counts.length);
    }

    /**
     * Retrieves a simple count of overdue books using raw JDBC.
     *
     * @return count of overdue borrow records
     */
    public int countOverdueJdbc() {
        String sql = "SELECT COUNT(*) FROM borrow_records " +
                     "WHERE status = 'BORROWED' AND due_date < ?";
        Integer count = jdbcTemplate.queryForObject(
            sql, Integer.class, Date.valueOf(LocalDate.now()));
        return count != null ? count : 0;
    }
}
