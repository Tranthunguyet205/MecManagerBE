package com.example.mecManager.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private Integer gender;
    private String profilePictureUrl;
    private Boolean isActive;
    private String role;
    private Date createdAt;
}
