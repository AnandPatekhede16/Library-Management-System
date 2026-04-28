package com.library.controller;

import com.library.dto.DashboardStatsDto;
import com.library.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the main dashboard page.
 * Admins see aggregated statistics; regular users see their personal borrow info.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final BookService        bookService;
    private final UserService        userService;
    private final BorrowService      borrowService;
    private final CategoryService    categoryService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication auth, Model model) {

        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Admin dashboard statistics
            DashboardStatsDto stats = new DashboardStatsDto(
                bookService.countBooks(),
                userService.countUsers(),
                borrowService.countBorrowed(),
                borrowService.countOverdue(),
                categoryService.count()
            );
            model.addAttribute("stats", stats);
            model.addAttribute("recentBorrows", borrowService.findAll()
                .stream().limit(10).toList());
            return "admin/dashboard";
        } else {
            // User dashboard: show their active borrows
            com.library.entity.User currentUser = userService.findByUsername(auth.getName());
            model.addAttribute("activeBorrows",
                borrowService.findActiveBorrowsByUser(currentUser.getId()));
            model.addAttribute("allBorrows",
                borrowService.findByUser(currentUser.getId()));
            return "user/dashboard";
        }
    }
}
