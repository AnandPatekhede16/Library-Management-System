package com.library.security;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Custom authentication provider that implements "auto-registration" on login.
 * If a normal user logs in with a name that doesn't exist, the system will seamlessly
 * create the account and log them in. The "admin" account remains strictly password protected.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // User exists. Verify password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Invalid username or password.");
            }
            if (!user.isEnabled()) {
                throw new DisabledException("Your account is disabled. Please contact the administrator.");
            }
            return createAuthToken(user, password);
        } else {
            // User does not exist.
            if ("admin".equalsIgnoreCase(username)) {
                // Do not auto-create the admin user if it's missing (it should be seeded).
                throw new BadCredentialsException("Invalid admin credentials.");
            }

            // Auto-create a new standard user on the fly!
            log.info("Auto-registering new user from login screen: {}", username);
            Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

            User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(username + "@library.com") // dummy email
                .fullName(username)               // default full name to username
                .enabled(true)
                .build();
            newUser.addRole(userRole);
            
            User savedUser = userRepository.save(newUser);
            return createAuthToken(savedUser, password);
        }
    }

    private Authentication createAuthToken(User user, String password) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(r.getName()))
            .collect(Collectors.toList());

        org.springframework.security.core.userdetails.User principal = 
            new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.isEnabled(),
                true, true, true, authorities);

        return new UsernamePasswordAuthenticationToken(principal, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
