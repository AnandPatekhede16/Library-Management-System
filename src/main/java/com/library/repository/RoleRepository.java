package com.library.repository;

import com.library.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Role} entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /** Retrieve a role by its name (e.g., "ROLE_ADMIN"). */
    Optional<Role> findByName(String name);
}
