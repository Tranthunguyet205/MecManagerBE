package com.example.mecManager.auth;

import java.security.Key;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.mecManager.model.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

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
                .claim("isActive", user.getIsActive())
                .claim("createdAt", user.getCreatedAt())
                .claim("updatedAt", user.getUpdatedAt())
                .claim("gender", user.getGender())

                .setIssuedAt(new Date()) // Ngày phát hành token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Hạn token
                .signWith(key)
                .compact();
    }

    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public boolean extractIsActive(String token) {
        Object active = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("isActive");

        if (active instanceof Boolean) {
            return (Boolean) active;
        } else if (active instanceof Integer) {
            return (Integer) active == 1;
        }
        return false;
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    public Long extractUserId(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.parseLong(subject);
    }

    // Xác thực token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true; // Token hợp lệ
        } catch (ExpiredJwtException e) {
            System.err.println("Token đã hết hạn: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token không đúng định dạng: " + e.getMessage());
        } catch (JwtException e) {
            System.err.println("Token hết hạn: " + e.getMessage());
        }
        return false;
    }
}
