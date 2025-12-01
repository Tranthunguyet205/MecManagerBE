package com.example.mecManager.dto.response;

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
    private String status;
    private String role;
    private Date createdAt;
}
