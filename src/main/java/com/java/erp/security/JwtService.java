package com.java.erp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Service for JWT token generation, parsing, and validation.
 */
@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final String issuer;

    public JwtService(
            @Value("${app.security.jwt.secret}") String jwtSecret,
            @Value("${app.security.jwt.expiration-ms}") long accessTokenExpirationMs,
            @Value("${app.security.jwt.refresh-expiration-ms}") long refreshTokenExpirationMs,
            @Value("${app.security.jwt.issuer}") String issuer) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes())));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.issuer = issuer;
    }

    /**
     * Generate an access token from an Authentication object.
     */
    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername(), userDetails, accessTokenExpirationMs, "ACCESS");
    }

    /**
     * Generate a refresh token from an Authentication object.
     */
    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername(), userDetails, refreshTokenExpirationMs, "REFRESH");
    }

    private String generateToken(String subject, CustomUserDetails userDetails, long expirationMs, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .claim("userId", userDetails.getId())
                .claim("name", userDetails.getName())
                .claim("roles", roles)
                .claim("tokenType", tokenType)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extract email (subject) from a JWT token.
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validate a JWT token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
