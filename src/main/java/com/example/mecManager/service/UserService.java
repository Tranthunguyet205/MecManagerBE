package com.example.mecManager.service;

import com.example.mecManager.model.LoginRequest;
import com.example.mecManager.model.LoginResponse;
import com.example.mecManager.model.RegisterRequest;
import com.example.mecManager.model.UserResponse;

public interface UserService {
    /**
     * Register a new user
     * 
     * @param request Registration request containing user details
     * @return UserResponse with registered user info
     */
    UserResponse register(RegisterRequest request);

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
}
