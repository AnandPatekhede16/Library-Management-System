package com.library.config;

import com.library.entity.Category;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.CategoryRepository;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with default roles, an admin user, and sample categories
 * the very first time the application starts.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository     roleRepository;
    private final UserRepository     userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder    passwordEncoder;

    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) {
        try {
            log.info("Fixing password column length in users table...");
            jdbcTemplate.execute("ALTER TABLE users MODIFY password VARCHAR(255)");
        } catch (Exception e) {
            log.warn("Could not alter users table: {}", e.getMessage());
        }

        // ── Roles ──────────────────────────────────────────────────────────────
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseGet(() -> {
                log.info("Seeding ROLE_ADMIN");
                return roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
            });

        roleRepository.findByName("ROLE_USER")
            .orElseGet(() -> {
                log.info("Seeding ROLE_USER");
                return roleRepository.save(Role.builder().name("ROLE_USER").build());
            });

        // ── Admin user ─────────────────────────────────────────────────────────
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@library.com")
                .fullName("System Administrator")
                .enabled(true)
                .build();
            admin.addRole(adminRole);
            userRepository.save(admin);
            log.info("Seeded admin user: admin / admin123");
        }

        // ── Default borrower user ──────────────────────────────────────────────
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
        if (!userRepository.existsByUsername("user")) {
            User normalUser = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .email("user@library.com")
                .fullName("Default Borrower")
                .enabled(true)
                .build();
            normalUser.addRole(userRole);
            userRepository.save(normalUser);
            log.info("Seeded normal user: user / user123");
        }

        // ── Test user: amit ────────────────────────────────────────────────────
        if (!userRepository.existsByUsername("amit")) {
            User amitUser = User.builder()
                .username("amit")
                .password(passwordEncoder.encode("amit@123"))
                .email("amit@library.com")
                .fullName("Amit Kumar")
                .enabled(true)
                .build();
            amitUser.addRole(userRole);
            userRepository.save(amitUser);
            log.info("Seeded normal user: amit / amit@123");
        }

        // ── Sample categories ──────────────────────────────────────────────────
        seedCategory("Computer",      "Computer Science and Programming");
        seedCategory("ENTC",          "Electronics and Telecommunications");
        seedCategory("Novels",        "Fictional stories and novels");
        seedCategory("Research Paper","Academic research and journals");
        seedCategory("Comics",        "Comic books and graphic novels");
        seedCategory("Entertainment", "Entertainment and pop culture");
        seedCategory("Science",       "Scientific books and journals");
        seedCategory("History",       "Historical books and biographies");
        seedCategory("Self-Help",     "Personal development and motivation");
    }

    private void seedCategory(String name, String description) {
        if (!categoryRepository.existsByName(name)) {
            log.info("Seeding category: {}", name);
            categoryRepository.save(Category.builder()
                .name(name)
                .description(description)
                .build());
        }
    }
}
