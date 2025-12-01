package com.example.mecManager.service;

import com.example.mecManager.common.enums.UserStatusEnum;
import com.example.mecManager.dto.request.LoginRequest;
import com.example.mecManager.dto.response.LoginResponse;
import com.example.mecManager.dto.request.RegisterRequest;
import com.example.mecManager.dto.response.UserResponse;

public interface UserService {
    /**
     * Register a new user and return JWT token
     * 
     * @param request Registration request containing user details
     * @return LoginResponse with JWT token and user info
     */
    LoginResponse register(RegisterRequest request);

    /**
     * Authenticate user and generate JWT token
     * 
     * @param request Login request containing username and password
     * @return LoginResponse with JWT token and user info
     */
    LoginResponse login(LoginRequest request);

    /**
     * Get user by ID
     * 
     * @param userId User ID
     * @return UserResponse with user information
     */
    UserResponse getUserById(Long userId);

    /**
     * Get current logged-in user profile
     * 
     * @return UserResponse with current user information
     */
    UserResponse getCurrentUserProfile();

    /**
     * Update user status (Admin only)
     * 
     * @param userId User ID to update
     * @param status New status (PENDING/APPROVED/REJECTED)
     * @return UserResponse with updated user information
     */
    UserResponse updateUserStatus(Long userId, UserStatusEnum status);
}
