package com.example.mecManager.auth;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.mecManager.auth.UserPrincipal;
import com.example.mecManager.common.enums.UserStatusEnum;
import com.example.mecManager.model.entity.DocInfo;
import com.example.mecManager.repository.DocInfoRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final DocInfoRepository docInfoRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, DocInfoRepository docInfoRepository) {
        this.jwtUtils = jwtUtils;
        this.docInfoRepository = docInfoRepository;
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
                        String status = jwtUtils.extractStatus(token);
                        Long userId = jwtUtils.extractUserId(token);
                        UserStatusEnum userStatus = UserStatusEnum.valueOf(status);

                        String requestURI = request.getRequestURI();
                        String method = request.getMethod();
                        
                        // Allow PENDING users to access profile creation/update endpoints
                        boolean isProfileEndpoint = (requestURI.contains("/doctors/") && method.equals("PUT")) ||
                                                   (requestURI.matches(".*/doctors$") && method.equals("POST"));
                        
                        // Block non-approved users except for profile endpoints
                        if (userStatus != UserStatusEnum.APPROVED && !isProfileEndpoint) {
                            log.warn("User {} is not approved (status: {})", username, status);
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"success\":false,\"message\":\"Tài khoản chưa được phê duyệt\"}");
                            return;
                        }

                        // For DOCTOR role, verify profile completeness for medical endpoints
                        if ("DOCTOR".equals(role) && requiresDoctorProfile(requestURI)) {
                            DocInfo docInfo = docInfoRepository.findByUserId(userId);
                            if (docInfo == null) {
                                log.warn("Doctor {} attempted to access {} without completed profile", username, requestURI);
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"success\":false,\"message\":\"Vui lòng hoàn thiện thông tin bác sĩ trước khi sử dụng chức năng này\"}");
                                return;
                            }
                        }

                        String authorityString = "ROLE_" + role;
                        List<SimpleGrantedAuthority> authorities = Collections
                                .singletonList(new SimpleGrantedAuthority(authorityString));

                        UserPrincipal principal = new UserPrincipal(userId, username, null, authorities, userStatus);
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

    /**
     * Check if the request URI requires doctor profile completion
     * Medical endpoints like prescription, patient management require complete doctor profile
     */
    private boolean requiresDoctorProfile(String requestURI) {
        return requestURI.contains("/prescription") || 
               requestURI.contains("/patient") ||
               requestURI.contains("/medicine");
    }
}
