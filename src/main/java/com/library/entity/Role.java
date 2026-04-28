package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a security role (e.g., ROLE_ADMIN, ROLE_USER).
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "users")   // Avoid circular reference in Lombok equals/hashCode
@ToString(exclude = "users")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name must follow Spring Security convention: ROLE_ADMIN, ROLE_USER.
     */
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    /** Owning side is on User – this is the inverse side. */
    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
