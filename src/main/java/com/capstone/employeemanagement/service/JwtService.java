package com.capstone.employeemanagement.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for generating and validating JSON Web Tokens (JWT).
 * Tokens carry information about the authenticated user and are signed using a
 * secret key loaded from application configuration.  The implementation uses
 * the jjwt library to build and parse tokens.  Stateless JWTs are preferred
 * over session‑based authentication in modern distributed systems because they
 * are scalable and secure【927998871578655†L118-L125】.
 */
@Service
public class JwtService {

    /**
     * Secret used to sign the JWT.  Should be at least 256 bits (32 bytes) for
     * the HS256 algorithm.  The value is expected to be Base64‑encoded and
     * configured in the application properties.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Token expiration time in milliseconds.
     */
    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Generates a JWT with custom claims and the provided user details.  The
     * username is stored as the subject and roles or other attributes can be
     * passed via extraClaims.  The token is signed using the secret key.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails details of the authenticated user
     * @return a signed JWT
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT for a user without any additional claims.
     *
     * @param userDetails authenticated user
     * @return JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    /**
     * Extracts the username (subject) from the given token.
     *
     * @param token the JWT
     * @return username encoded in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a claim using the provided claims resolver function.
     *
     * @param token the JWT
     * @param claimsResolver function to retrieve a specific claim
     * @param <T> type of the claim
     * @return the claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates the token by comparing the username and checking the expiration
     * time.  Returns true only if the token belongs to the given user and is
     * not expired.
     *
     * @param token the JWT to validate
     * @param userDetails the authenticated user
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Returns true if the token has expired.
     *
     * @param token the JWT
     * @return true if expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT
     * @return expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses all claims from the token using the signing key.  If the token is
     * malformed or the signature is invalid an exception will be thrown.
     *
     * @param token JWT to parse
     * @return claims contained in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Decodes the Base64 encoded secret and returns it as a {@link Key}.
     *
     * @return signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
}