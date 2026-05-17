package com.library.service;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRecord.BorrowStatus;
import com.library.entity.User;
import com.library.exception.LibraryException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles borrowing and returning of books.
 * All operations are wrapped in transactions to keep book copy counts consistent.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BorrowService {

    private static final int DEFAULT_LOAN_DAYS = 14;

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookService            bookService;
    private final UserService            userService;
    private final EmailService           emailService;

    // ── Borrow a book ─────────────────────────────────────────────────────────

    /**
     * Creates a borrow record, decrements available copies.
     *
     * @param username  the borrowing user's username
     * @param bookId    id of the book to borrow
     * @return          the saved BorrowRecord
     */
    public BorrowRecord borrowBook(String username, Long bookId) {
        User user = userService.findByUsername(username);
        Book book = bookService.findById(bookId);

        // Prevent duplicate active borrow
        if (borrowRecordRepository.existsByUserIdAndBookIdAndStatus(
                user.getId(), bookId, BorrowStatus.BORROWED)) {
            throw new LibraryException(
                "You have already borrowed '" + book.getTitle() + "' and not returned it yet.");
        }

        // Decrement copy count (throws if 0 available)
        bookService.decrementAvailable(book);

        BorrowRecord record = BorrowRecord.builder()
            .user(user)
            .book(book)
            .borrowDate(LocalDate.now())
            .dueDate(LocalDate.now().plusDays(DEFAULT_LOAN_DAYS))
            .status(BorrowStatus.BORROWED)
            .build();

        BorrowRecord saved = borrowRecordRepository.save(record);
        log.info("User '{}' borrowed book '{}' (record id={})",
            username, book.getTitle(), saved.getId());

        // Send borrow confirmation email (async – failure won't affect the borrow transaction)
        try {
            emailService.sendBorrowConfirmation(saved);
        } catch (Exception e) {
            log.warn("Could not send borrow confirmation email: {}", e.getMessage());
        }

        return saved;
    }

    // ── Return a book ─────────────────────────────────────────────────────────

    /**
     * Marks a borrow record as returned and increments available copies.
     *
     * @param recordId  id of the BorrowRecord to close
     * @param username  the user initiating the return (validated against record owner)
     */
    public BorrowRecord returnBook(Long recordId, String username) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", recordId));

        // Security check: only the borrower (or admin via separate path) can return
        if (!record.getUser().getUsername().equals(username)) {
            throw new LibraryException("You are not authorised to return this record.");
        }
        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new LibraryException("This book has already been returned.");
        }

        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);
        borrowRecordRepository.save(record);

        bookService.incrementAvailable(record.getBook());
        log.info("Book '{}' returned by '{}'", record.getBook().getTitle(), username);
        return record;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /** Admin: all borrow records. */
    @Transactional(readOnly = true)
    public List<BorrowRecord> findAll() {
        List<BorrowRecord> records = borrowRecordRepository.findAll();
        records.forEach(r -> {
            r.getBook().getTitle();
            r.getUser().getUsername();
        });
        return records;
    }

    /** All records for a specific user. */
    @Transactional(readOnly = true)
    public List<BorrowRecord> findByUser(Long userId) {
        List<BorrowRecord> records = borrowRecordRepository.findByUserId(userId);
        records.forEach(r -> r.getBook().getTitle());
        return records;
    }

    /** Currently active borrows for a user. */
    @Transactional(readOnly = true)
    public List<BorrowRecord> findActiveBorrowsByUser(Long userId) {
        List<BorrowRecord> records = borrowRecordRepository.findByUserIdAndStatus(userId, BorrowStatus.BORROWED);
        records.forEach(r -> r.getBook().getTitle());
        return records;
    }

    /** Records where due date < today and still BORROWED. */
    @Transactional(readOnly = true)
    public List<BorrowRecord> findOverdue() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now());
    }

    /** Count currently borrowed books (for dashboard). */
    @Transactional(readOnly = true)
    public long countBorrowed() {
        return borrowRecordRepository.countByStatus(BorrowStatus.BORROWED);
    }

    /** Count overdue records (for dashboard). */
    @Transactional(readOnly = true)
    public long countOverdue() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now()).size();
    }

    /** Admin returns on behalf of user. */
    public BorrowRecord adminReturn(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", recordId));
        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new LibraryException("Already returned.");
        }
        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);
        borrowRecordRepository.save(record);
        bookService.incrementAvailable(record.getBook());
        return record;
    }
}
