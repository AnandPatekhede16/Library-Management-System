package com.library.service;

import com.library.dto.BookDto;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.exception.LibraryException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for book management (CRUD + search).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository     bookRepository;
    private final CategoryRepository categoryRepository;

    // ── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        book.getReviews().size(); // Initialize lazy collection
        return book;
    }

    @Transactional(readOnly = true)
    public List<Book> search(String keyword, Long categoryId) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        if (categoryId != null) {
            if (hasKeyword) {
                return bookRepository.searchByKeywordAndCategoryId(keyword.trim(), categoryId);
            } else {
                return bookRepository.findByCategoryId(categoryId);
            }
        }
        if (hasKeyword) {
            return bookRepository.searchByKeyword(keyword.trim());
        }
        return findAll();
    }

    @Transactional(readOnly = true)
    public List<Book> findAvailable() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }

    @Transactional(readOnly = true)
    public long countBooks() {
        return bookRepository.count();
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public Book createBook(BookDto dto) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new LibraryException("A book with ISBN '" + dto.getIsbn() + "' already exists.");
        }
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        Book book = Book.builder()
            .title(dto.getTitle())
            .author(dto.getAuthor())
            .isbn(dto.getIsbn())
            .totalCopies(dto.getTotalCopies())
            .availableCopies(dto.getTotalCopies())   // all copies available initially
            .description(dto.getDescription())
            .publishedYear(dto.getPublishedYear())
            .category(category)
            .build();

        Book saved = bookRepository.save(book);
        log.info("Created book: '{}' (id={})", saved.getTitle(), saved.getId());
        return saved;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public Book updateBook(Long id, BookDto dto) {
        Book book = findById(id);
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        // If ISBN changed, check uniqueness
        if (!book.getIsbn().equals(dto.getIsbn()) && bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new LibraryException("A book with ISBN '" + dto.getIsbn() + "' already exists.");
        }

        // Adjust available copies proportionally when total changes
        int delta = dto.getTotalCopies() - book.getTotalCopies();
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + delta));

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setTotalCopies(dto.getTotalCopies());
        book.setDescription(dto.getDescription());
        book.setPublishedYear(dto.getPublishedYear());
        book.setCategory(category);

        return bookRepository.save(book);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void deleteBook(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
        log.info("Deleted book id={}", id);
    }

    // ── Internal helpers (used by BorrowService) ──────────────────────────────

    /** Decrease available copies when a book is borrowed. */
    public void decrementAvailable(Book book) {
        if (book.getAvailableCopies() <= 0) {
            throw new LibraryException("No available copies for '" + book.getTitle() + "'.");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    /** Increase available copies when a book is returned. */
    public void incrementAvailable(Book book) {
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }
}
