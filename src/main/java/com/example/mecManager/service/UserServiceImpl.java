package com.example.mecManager.service;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mecManager.common.constants.AppConstants;
import com.example.mecManager.common.enums.RoleEnum;
import com.example.mecManager.common.enums.UserStatusEnum;
import com.example.mecManager.auth.JwtUtils;
import com.example.mecManager.dto.request.LoginRequest;
import com.example.mecManager.dto.response.LoginResponse;
import com.example.mecManager.dto.request.RegisterRequest;
import com.example.mecManager.model.entity.User;
import com.example.mecManager.dto.response.UserResponse;
import com.example.mecManager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Tên đăng nhập không được để trống");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }

        String username = request.getUsername().trim().toLowerCase();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Tên đăng nhập này đã được sử dụng. Vui lòng chọn tên khác.");
        }

        RoleEnum role = (request.getRole() != null) ? request.getRole() : RoleEnum.DOCTOR;

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(username);
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setStatus(user.getRole() == RoleEnum.ADMIN ? UserStatusEnum.APPROVED : UserStatusEnum.PENDING);
        user.setProfilePictureUrl(AppConstants.URL.IMG_URL);
        user.setGender(AppConstants.GENDER.OTHER);
        user.setCreatedAt(new Date());

        User savedUser = userRepository.save(user);

        String token = jwtUtils.generateToken(savedUser);
        UserResponse userResponse = mapToUserResponse(savedUser);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userResponse)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        if (user.getStatus() != UserStatusEnum.APPROVED) {
            throw new RuntimeException("Tài khoản chưa được phê duyệt. Vui lòng liên hệ admin.");
        }

        String token = jwtUtils.generateToken(user);
        UserResponse userResponse = mapToUserResponse(user);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        return mapToUserResponse(user);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Không tìm thấy người dùng được xác thực");
        }
        return auth.getName();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .profilePictureUrl(user.getProfilePictureUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public UserResponse updateUserStatus(Long userId, UserStatusEnum status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        user.setStatus(status);
        user.setUpdatedAt(new Date());
        
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
}
