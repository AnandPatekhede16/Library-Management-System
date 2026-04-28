package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Book} entities.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /** Full-text-like search across title and author (case-insensitive). */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT b FROM Book b WHERE b.category.id = :categoryId AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Book> searchByKeywordAndCategoryId(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);


    /** Find books by category. */
    List<Book> findByCategoryId(Long categoryId);

    /** Find all books that have at least one copy available. */
    List<Book> findByAvailableCopiesGreaterThan(int count);

    boolean existsByIsbn(String isbn);
}
