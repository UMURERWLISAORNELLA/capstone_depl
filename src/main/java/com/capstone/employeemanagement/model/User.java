package com.capstone.employeemanagement.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entity representing an authenticated user of the system.  Users have a
 * unique username and email, a hashed password and a {@link Role} defining
 * their access level.  By implementing the {@link UserDetails} interface the
 * entity can be used directly by Spring Security for authentication and
 * authorization.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Returns the authorities granted to the user.  The role name is
     * automatically prefixed with {@code ROLE_} to match Spring Security's
     * convention for granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Accounts are never expired in this simple example.  Override if you need
     * more sophisticated account lifecycle handling.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Accounts are never locked in this simple example.  Override if you need
     * account locking.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Credentials are never expired.  Override if you need password rotation.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * All accounts are enabled by default.  Override if you need to disable
     * accounts.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}