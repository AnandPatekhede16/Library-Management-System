package com.library.config;

import com.library.entity.User;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebugRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        userRepository.findByUsername("amit").ifPresent(user -> {
            log.info("DEBUG USER: {}", user.getUsername());
            log.info("DEBUG USER PASSWORD HASH: {}", user.getPassword());
            log.info("DEBUG USER MATCHES amit@123: {}", passwordEncoder.matches("amit@123", user.getPassword()));
            log.info("DEBUG USER ROLES: {}", user.getRoles());
            log.info("DEBUG USER ENABLED: {}", user.isEnabled());
        });
    }
}
