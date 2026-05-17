package com.library.controller;

import com.library.dto.BookDto;
import com.library.entity.Book;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles book listing, search (for all users) and CRUD (for ADMIN).
 */
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService        bookService;
    private final CategoryService    categoryService;
    private final BorrowService      borrowService;
    private final com.library.repository.ReviewRepository reviewRepository;
    private final com.library.repository.UserRepository   userRepository;

    // ── Public: list / search ────────────────────────────────────────────────

    @GetMapping
    public String listBooks(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) Long categoryId,
                            Model model) {
        model.addAttribute("books",   bookService.search(keyword, categoryId));
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "books/list";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviewRepository.findByBookIdOrderByCreatedAtDesc(id));
        return "books/detail";
    }

    // ── User: borrow & review ────────────────────────────────────────────────

    @PostMapping("/{id}/borrow")
    public String borrowBook(@PathVariable Long id, Authentication auth,
                              RedirectAttributes ra) {
        try {
            borrowService.borrowBook(auth.getName(), id);
            ra.addFlashAttribute("successMsg", "Book borrowed successfully! Due in 14 days.");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/review")
    public String addReview(@PathVariable Long id,
                            @RequestParam int rating,
                            @RequestParam(required = false) String comment,
                            Authentication auth,
                            RedirectAttributes ra) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            ra.addFlashAttribute("errorMsg", "You must be logged in to leave a review.");
            return "redirect:/login";
        }
        
        try {
            com.library.entity.User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
            Book book = bookService.findById(id);

            com.library.entity.Review review = com.library.entity.Review.builder()
                .rating(rating)
                .comment(comment)
                .book(book)
                .user(user)
                .build();
            reviewRepository.save(review);
            
            ra.addFlashAttribute("successMsg", "Your review has been submitted!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Failed to submit review.");
        }
        return "redirect:/books/" + id;
    }

    // ── Admin: create ────────────────────────────────────────────────────────

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addBookForm(@RequestParam(required = false) Long categoryId, Model model) {
        BookDto dto = new BookDto();
        dto.setCategoryId(categoryId);
        model.addAttribute("book",       dto);
        model.addAttribute("categories", categoryService.findAll());
        return "books/form";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveBook(@Valid @ModelAttribute("book") BookDto dto,
                            BindingResult result, Model model,
                            RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "books/form";
        }
        try {
            bookService.createBook(dto);
            ra.addFlashAttribute("successMsg", "Book added successfully.");
            return "redirect:/books";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errorMsg", e.getMessage());
            return "books/form";
        }
    }

    // ── Admin: edit ──────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setDescription(book.getDescription());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setCategoryId(book.getCategory().getId());

        model.addAttribute("book",       dto);
        model.addAttribute("categories", categoryService.findAll());
        return "books/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateBook(@PathVariable Long id,
                              @Valid @ModelAttribute("book") BookDto dto,
                              BindingResult result, Model model,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "books/form";
        }
        try {
            bookService.updateBook(id, dto);
            ra.addFlashAttribute("successMsg", "Book updated successfully.");
            return "redirect:/books";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errorMsg", e.getMessage());
            return "books/form";
        }
    }

    // ── Admin: delete ────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBook(@PathVariable Long id, RedirectAttributes ra) {
        bookService.deleteBook(id);
        ra.addFlashAttribute("successMsg", "Book deleted.");
        return "redirect:/books";
    }
}
