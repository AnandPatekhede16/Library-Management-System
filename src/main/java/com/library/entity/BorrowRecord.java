package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Tracks each borrowing event: who borrowed which book, when, and the due / return date.
 *
 * ManyToOne → User
 * ManyToOne → Book
 */
@Entity
@Table(name = "borrow_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who borrowed the book. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    /** The book that was borrowed. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Book book;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    /** Default loan period is 14 days. */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /** Null until the book is physically returned. */
    @Column(name = "return_date")
    private LocalDate returnDate;

    /**
     * Current status of the borrow record.
     * BORROWED → book is still with the user.
     * RETURNED → book has been returned.
     * OVERDUE  → due date has passed and book not returned.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BorrowStatus status = BorrowStatus.BORROWED;

    public enum BorrowStatus {
        BORROWED, RETURNED, OVERDUE
    }
}
