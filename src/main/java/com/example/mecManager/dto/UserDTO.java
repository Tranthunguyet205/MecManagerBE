package com.example.mecManager.dto;

import com.example.mecManager.model.entity.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {

    private Long id;

    private String username;

    private String fullName;

    private String password;

    private Integer gender;

    private Role role;

    private Date createdAt;

    private Date updatedAt;

    private Long updatedUserId;

    private Long createdUserId;


    private String profilePictureUrl;

    private String status;

    private String email;

    

}
