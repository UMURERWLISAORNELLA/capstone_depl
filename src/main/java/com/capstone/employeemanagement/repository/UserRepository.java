package com.capstone.employeemanagement.repository;

import com.capstone.employeemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for performing CRUD operations on {@link User} entities.  Extending
 * {@link JpaRepository} gives us methods such as {@code findAll}, {@code save}
 * and {@code deleteById} without writing any implementation code.  Additional
 * query methods are defined based on property names to allow lookup by
 * username or email.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}