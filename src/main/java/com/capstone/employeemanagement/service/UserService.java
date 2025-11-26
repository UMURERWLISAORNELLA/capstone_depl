package com.capstone.employeemanagement.service;

import com.capstone.employeemanagement.dto.*;
import com.capstone.employeemanagement.model.Role;
import com.capstone.employeemanagement.model.User;
import com.capstone.employeemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;

import java.util.Optional;

/**
 * Service encapsulating all user‑related business logic: registration,
 * authentication and loading user details for security.  The service uses
 * {@link UserRepository} for persistence, {@link PasswordEncoder} for hashing
 * passwords and {@link JwtService} for generating access tokens.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registers a new user account.  If the username or email already exists
     * a {@link IllegalArgumentException} is thrown.  The password is hashed
     * using the configured {@link PasswordEncoder} before persistence.  A
     * JWT is returned along with the newly created user’s public details.
     *
     * @param request user registration data
     * @return authentication response containing the token and user info
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // validate uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // build the new user
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        User saved = userRepository.save(newUser);

        // generate JWT token for the newly created user
        String token = jwtService.generateToken(saved);
        long expiresAt = jwtService.extractClaim(token, Claims::getExpiration).getTime();
        return AuthenticationResponse.builder()
                .accessToken(token)
                .expiresAt(expiresAt)
                .user(toUserResponse(saved))
                .build();
    }

    /**
     * Authenticates an existing user.  Matches the provided password against
     * the stored hash and returns a JWT if the credentials are valid.
     *
     * @param request authentication request containing username/email and password
     * @return authentication response with token and user details
     */
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String identifier = request.getUsernameOrEmail();
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(identifier, identifier);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(user);
        long expiresAt = jwtService.extractClaim(token, Claims::getExpiration).getTime();
        return AuthenticationResponse.builder()
                .accessToken(token)
                .expiresAt(expiresAt)
                .user(toUserResponse(user))
                .build();
    }

    /**
     * Returns the expiration timestamp from a freshly generated token.
     *
     * @param token JWT token
     * @return expiration in milliseconds since epoch
     */
    private long savedJwtExpiry(String token) {
        return jwtService.extractClaim(token, Claims::getExpiration).getTime();
    }

    /**
     * Converts a {@link User} entity into a lightweight {@link UserResponse} DTO.
     */
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    /**
     * Locates the user based on the username or email.  This method is
     * automatically used by Spring Security during authentication.
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated user record
     * @throws UsernameNotFoundException if the user could not be found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}