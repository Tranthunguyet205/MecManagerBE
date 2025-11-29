package com.example.mecManager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mecManager.Common.ApiResponse;
import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.LoginRequest;
import com.example.mecManager.model.LoginResponse;
import com.example.mecManager.model.RegisterRequest;
import com.example.mecManager.model.UserResponse;
import com.example.mecManager.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AppConstants.URL.API_BASE + "/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Register a new user
     * 
     * @param request Registration request with username, password, fullName, email,
     *                phone
     * @return ApiResponse with registered user info
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserResponse userResponse = userService.register(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", userResponse));
        } catch (DataIntegrityViolationException e) {
            String message = "Dữ liệu đã tồn tại";
            if (e.getMessage().contains("username")) {
                message = "Tên đăng nhập này đã được sử dụng";
            } else if (e.getMessage().contains("email")) {
                message = "Email này đã được đăng ký";
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, message));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống. Vui lòng thử lại sau."));
        }
    }

    /**
     * User login
     * 
     * @param request Login request with username and password
     * @return ApiResponse with JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = userService.login(request);
            return ResponseEntity
                    .ok(ApiResponse.success("Login successful", loginResponse));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống. Vui lòng thử lại sau."));
        }
    }

    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return ApiResponse with user information
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            UserResponse userResponse = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }
}
