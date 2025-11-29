package com.example.mecManager.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Patient Profile API responses
 * Excludes sensitive data and only includes user IDs instead of full user
 * objects
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {

  private Long id;

  private String fullName;

  @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
  private Date dob;

  private Integer gender;

  private String address;

  private String phone;

  private String note;

  private Integer treatmentType;

  private Float heightCm;

  private Float weightKg;

  private String diagnosis;

  private String conclusion;

  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  private Date createdAt;

  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  private Date updatedAt;

  private Long createdBy;

  private Long updatedBy;
}
