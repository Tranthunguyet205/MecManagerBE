package com.example.mecManager.auth;

import java.security.Key;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.mecManager.model.entity.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {

    private static final String SECRET_KEY = "MecManagerSecretKeyForJWTTokenSigningPurposeVeryLongAndSecureKeyThatCannotBeGuessedEasily12345";
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final long EXPIRATION_TIME = 86400000; // 1 ngày

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .claim("fullName", user.getFullName())
                .claim("profilePictureUrl", user.getProfilePictureUrl())
                .claim("status", user.getStatus())
                .claim("createdAt", user.getCreatedAt())
                .claim("updatedAt", user.getUpdatedAt())
                .claim("gender", user.getGender())

                .setIssuedAt(new Date()) // Ngày phát hành token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Hạn token
                .signWith(key)
                .compact();
    }

    public String extractRole(String token) {
        try {
            String role = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
            return role;
        } catch (Exception e) {
            throw e;
        }
    }

    public String extractStatus(String token) {
        try {
            Object status = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("status");

            if (status instanceof String) {
                return (String) status;
            } else {
                return "PENDING"; // Default fallback
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String extractUsername(String token) {
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("username", String.class);
            return username;
        } catch (Exception e) {
            throw e;
        }
    }

    public Long extractUserId(String token) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            Long userId = Long.parseLong(subject);
            return userId;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired");
        } catch (MalformedJwtException e) {
            log.warn("Malformed token");
        } catch (JwtException e) {
            log.warn("JWT validation failed");
        } catch (Exception e) {
            log.error("Unexpected error validating token", e);
        }
        return false;
    }
}
