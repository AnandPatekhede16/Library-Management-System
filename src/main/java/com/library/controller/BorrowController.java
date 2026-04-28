package com.library.controller;

import com.library.service.BorrowService;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Manages borrow/return actions and record viewing.
 */
@Controller
@RequestMapping("/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;
    private final UserService   userService;

    // ── User: my borrowed books ───────────────────────────────────────────────

    @GetMapping("/my")
    public String myBorrows(Authentication auth, Model model) {
        var currentUser = userService.findByUsername(auth.getName());
        model.addAttribute("records", borrowService.findByUser(currentUser.getId()));
        return "borrows/my-borrows";
    }

    // ── User: return a book ───────────────────────────────────────────────────

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, Authentication auth,
                              RedirectAttributes ra) {
        try {
            borrowService.returnBook(id, auth.getName());
            ra.addFlashAttribute("successMsg", "Book returned successfully. Thank you!");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:/borrows/my";
    }

    // ── Admin: all borrow records ─────────────────────────────────────────────

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public String allBorrows(Model model) {
        model.addAttribute("records", borrowService.findAll());
        return "borrows/all-borrows";
    }

    // ── Admin: overdue records ────────────────────────────────────────────────

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public String overdueBorrows(Model model) {
        model.addAttribute("records", borrowService.findOverdue());
        return "borrows/overdue";
    }

    // ── Admin: process return on behalf of user ───────────────────────────────

    @PostMapping("/{id}/admin-return")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminReturn(@PathVariable Long id, RedirectAttributes ra) {
        try {
            borrowService.adminReturn(id);
            ra.addFlashAttribute("successMsg", "Book marked as returned.");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:/borrows/all";
    }
}
