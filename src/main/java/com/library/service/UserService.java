package com.library.service;

import com.library.dto.UserRegistrationDto;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.exception.LibraryException;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for user management and Spring Security's UserDetailsService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository     userRepository;
    private final RoleRepository     roleRepository;
    private final PasswordEncoder    passwordEncoder;

    // ── Spring Security integration ──────────────────────────────────────────

    /**
     * Called by Spring Security during login to load user credentials and authorities.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            true, true, true,
            authorities
        );
    }

    // ── Registration ─────────────────────────────────────────────────────────

    /**
     * Registers a new user with the ROLE_USER role.
     */
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new LibraryException("Username '" + dto.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new LibraryException("Email '" + dto.getEmail() + "' is already registered.");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new LibraryException("Passwords do not match.");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new LibraryException("ROLE_USER not found. Run data initializer first."));

        User user = User.builder()
            .username(dto.getUsername())
            .password(passwordEncoder.encode(dto.getPassword()))
            .email(dto.getEmail())
            .fullName(dto.getFullName())
            .enabled(true)
            .build();

        user.addRole(userRole);
        User saved = userRepository.save(user);
        log.info("Registered new user: {}", saved.getUsername());
        return saved;
    }

    // ── Admin operations ──────────────────────────────────────────────────────

    /** Retrieve all users (for admin management page). */
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /** Retrieve a user by its primary key. */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new com.library.exception.ResourceNotFoundException("User", id));
    }

    /** Toggle user enabled/disabled. */
    public void toggleUserStatus(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        log.info("Toggled status for user {}: enabled={}", user.getUsername(), user.isEnabled());
    }

    /** Promote a regular user to ADMIN role. */
    public void promoteToAdmin(Long id) {
        User user = findById(id);
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new LibraryException("ROLE_ADMIN not found."));
        user.addRole(adminRole);
        userRepository.save(user);
    }

    /** Find user entity by username. */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new com.library.exception.ResourceNotFoundException(
                "User not found: " + username));
    }

    /** Count all users. */
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }
}
