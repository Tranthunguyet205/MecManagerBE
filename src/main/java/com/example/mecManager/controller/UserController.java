package com.example.mecManager.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(AppConstants.URL.API_BASE + "/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Register a new user
     * 
     * @param request Registration request with username, password, fullName, phone
     * @return ApiResponse with JWT token and user info
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user account", description = "Register new user account and receive JWT token. User can proceed without login to update personal information.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Username already exists or invalid data")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse loginResponse = userService.register(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đăng ký thành công. Vui lòng tiếp tục cập nhật thông tin cá nhân.",
                            loginResponse));
        } catch (DataIntegrityViolationException e) {
            String message = "Dữ liệu đã tồn tại";
            if (e.getMessage() != null && e.getMessage().contains("username")) {
                message = "Tên đăng nhập này đã được sử dụng";
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

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username and password. Returns JWT token on success.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
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
                    .body(ApiResponse.error(500, "System error. Please try again later."));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
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

    /**
     * Get current logged-in user profile
     * 
     * @return ApiResponse with current user information
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user profile", description = "Retrieve currently authenticated user's profile information. Requires valid JWT token.")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthenticated / Token expired"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentProfile() {
        try {
            UserResponse userResponse = userService.getCurrentUserProfile();
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, e.getMessage()));
        }
    }
}
