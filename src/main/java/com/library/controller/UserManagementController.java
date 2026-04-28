package com.library.controller;

import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin-only controller for managing registered users.
 */
@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @PostMapping("/{id}/toggle")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleUserStatus(id);
        ra.addFlashAttribute("successMsg", "User status updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/promote")
    public String promoteToAdmin(@PathVariable Long id, RedirectAttributes ra) {
        userService.promoteToAdmin(id);
        ra.addFlashAttribute("successMsg", "User promoted to Admin.");
        return "redirect:/admin/users";
    }
}
