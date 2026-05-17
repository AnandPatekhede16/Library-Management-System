package com.library.repository;

import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRecord.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for {@link BorrowRecord} entities.
 */
@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    /** All records for a specific user. */
    List<BorrowRecord> findByUserId(Long userId);

    /** All active (non-returned) records for a user. */
    List<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status);

    /** All records with a given status (used for overdue checks). */
    List<BorrowRecord> findByStatus(BorrowStatus status);

    /** Records where due date has passed and book is still BORROWED – used by scheduler. */
    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate < :today AND br.status = 'BORROWED'")
    List<BorrowRecord> findOverdueRecords(@Param("today") LocalDate today);

    /** Total number of currently issued (BORROWED) books. */
    long countByStatus(BorrowStatus status);

    /** Check whether a user already has an active borrow for a specific book. */
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, BorrowStatus status);

    /** Records due exactly on targetDate that are still BORROWED – used by 3-day pre-due scheduler. */
    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate = :targetDate AND br.status = 'BORROWED'")
    List<BorrowRecord> findRecordsDueOn(@Param("targetDate") LocalDate targetDate);
}
