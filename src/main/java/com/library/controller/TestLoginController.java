package com.library.controller;

import com.library.dto.UserRegistrationDto;
import com.library.entity.User;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestLoginController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/test-reg")
    public String testReg() {
        try {
            UserRegistrationDto dto = new UserRegistrationDto();
            dto.setUsername("testuser123");
            dto.setPassword("password");
            dto.setConfirmPassword("password");
            dto.setEmail("test12345@test.com");
            dto.setFullName("Test User");
            
            User saved = userService.registerUser(dto);
            
            UserDetails ud = userService.loadUserByUsername("testuser123");
            
            boolean matches = passwordEncoder.matches("password", saved.getPassword());
            
            return "Saved user: " + saved.getUsername() + ", enabled: " + saved.isEnabled() + 
                   ", roles: " + saved.getRoles().size() + 
                   ", authorities: " + ud.getAuthorities() + 
                   ", passwordMatch: " + matches;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
