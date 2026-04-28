package com.library.repository;

import com.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find user by username (used by Spring Security). */
    Optional<User> findByUsername(String username);

    /** Check if a username already exists. */
    boolean existsByUsername(String username);

    /** Check if an email already exists. */
    boolean existsByEmail(String email);

    /** Find all users with a given role name. */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findAllByRoleName(String roleName);
}
