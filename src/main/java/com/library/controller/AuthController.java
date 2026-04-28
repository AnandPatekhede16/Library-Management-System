package com.library.controller;

import com.library.dto.UserRegistrationDto;
import com.library.exception.LibraryException;
import com.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles public-facing authentication pages: login and registration.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // ── Login ────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                             @RequestParam(required = false) String logout,
                             Model model) {
        if (error != null)  model.addAttribute("errorMsg", "Invalid username or password.");
        if (logout != null) model.addAttribute("logoutMsg", "You have been logged out.");
        return "auth/login";
    }

    // ── Register ─────────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                                       BindingResult result,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("successMsg",
                "Registration successful! Please login.");
            return "redirect:/login";
        } catch (LibraryException ex) {
            result.rejectValue("username", "error.user", ex.getMessage());
            return "auth/register";
        }
    }

    // ── Access denied ────────────────────────────────────────────────────────

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
