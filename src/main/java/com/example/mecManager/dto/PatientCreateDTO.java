package com.example.mecManager.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for patient profile creation/update
 * Contains only required fields with proper validation
 * 
 * Required fields:
 * - fullName: Patient's full name (1-100 chars)
 * - dob: Date of birth (must be in past)
 * - gender: 0 = Male, 1 = Female
 * - address: Patient's address (1-255 chars)
 * - diagnosis: Medical diagnosis (1-255 chars)
 * - treatmentType: 0 = Outpatient, 1 = Inpatient
 * 
 * Optional fields:
 * - phone: Phone number (10-11 digits)
 * - note: Additional notes
 * - heightCm: Height in centimeters
 * - weightKg: Weight in kilograms
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientCreateDTO {

  @NotBlank(message = "Tên bệnh nhân không được để trống")
  @Size(min = 1, max = 100, message = "Tên bệnh nhân phải từ 1-100 ký tự")
  private String fullName;

  @NotNull(message = "Ngày sinh không được để trống")
  @PastOrPresent(message = "Ngày sinh không được trong tương lai")
  @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
  private Date dob;

  @NotNull(message = "Giới tính không được để trống")
  @Min(value = 0, message = "Giới tính không hợp lệ")
  @Max(value = 1, message = "Giới tính không hợp lệ")
  private Integer gender; // 0 = Male, 1 = Female

  @NotBlank(message = "Địa chỉ không được để trống")
  @Size(min = 1, max = 255, message = "Địa chỉ phải từ 1-255 ký tự")
  private String address;

  @Pattern(regexp = "^$|^\\d{10,11}$", message = "Số điện thoại phải từ 10-11 chữ số")
  private String phone; // Optional, 10-11 digits

  @NotBlank(message = "Chẩn đoán không được để trống")
  @Size(min = 1, max = 255, message = "Chẩn đoán phải từ 1-255 ký tự")
  private String diagnosis;

  @NotNull(message = "Loại điều trị không được để trống")
  @Min(value = 0, message = "Loại điều trị không hợp lệ")
  @Max(value = 1, message = "Loại điều trị không hợp lệ")
  private Integer treatmentType; // 0 = Outpatient (Ngoại trú), 1 = Inpatient (Nội trú)

  private String note; // Optional

  @Min(value = 0, message = "Chiều cao không hợp lệ")
  private Float heightCm; // Optional

  @Min(value = 0, message = "Cân nặng không hợp lệ")
  private Float weightKg; // Optional

}
