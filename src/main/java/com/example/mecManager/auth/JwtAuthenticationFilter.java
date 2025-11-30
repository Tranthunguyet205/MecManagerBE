package com.example.mecManager.auth;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.mecManager.model.UserPrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtUtils.validateToken(token)) {
                    try {
                        String username = jwtUtils.extractUsername(token);
                        String role = jwtUtils.extractRole(token);
                        Long userId = jwtUtils.extractUserId(token);
                        Boolean isActive = jwtUtils.extractIsActive(token);

                        if (!isActive) {
                            log.warn("User {} is not active", username);
                            filterChain.doFilter(request, response);
                            return;
                        }

                        String authorityString = "ROLE_" + role;
                        List<SimpleGrantedAuthority> authorities = Collections
                                .singletonList(new SimpleGrantedAuthority(authorityString));

                        UserPrincipal principal = new UserPrincipal(userId, username, null, authorities, isActive);
                        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                        authentication.setDetails(userId);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (Exception e) {
                        log.error("Error extracting token claims", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT filter error", e);
        }
        
        filterChain.doFilter(request, response);
    }
}
