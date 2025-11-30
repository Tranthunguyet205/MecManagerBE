package com.example.mecManager.model;

import com.example.mecManager.Common.enums.RoleEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating user accounts by ADMIN
 * Only ADMIN role can use this endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String password;

    private String fullName;

    private String phone;

    @NotNull(message = "Vai trò không được để trống")
    private RoleEnum role;

    private Boolean isActive;
}
