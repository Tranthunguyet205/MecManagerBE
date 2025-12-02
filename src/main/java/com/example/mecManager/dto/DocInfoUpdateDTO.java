package com.example.mecManager.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating doctor information
 * Only includes editable fields, userId can be provided for creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocInfoUpdateDTO {

  // User ID (required for creation, optional for updates)
  private Long userId;

  @NotBlank(message = "Tên đầy đủ không được để trống")
  private String fullName;

  @NotNull(message = "Ngày sinh không được để trống")
  private Date dob;

  @NotBlank(message = "Số điện thoại không được để trống")
  private String phone;

  @NotBlank(message = "CCCD không được để trống")
  private String cccd;

  @NotNull(message = "Ngày cấp CCCD không được để trống")
  private Date cccdIssueDate;

  @NotBlank(message = "Nơi cấp CCCD không được để trống")
  private String cccdIssuePlace;

  @NotBlank(message = "Địa chỉ hiện tại không được để trống")
  private String currentAddress;

  @NotBlank(message = "Email không được để trống")
  private String email;

  @NotBlank(message = "Số chứng chỉ hành nghề không được để trống")
  private String practiceCertificateNo;

  @NotNull(message = "Ngày cấp chứng chỉ hành nghề không được để trống")
  private Date practiceCertificateIssueDate;

  @NotBlank(message = "Nơi cấp chứng chỉ hành nghề không được để trống")
  private String practiceCertificateIssuePlace;

  @NotBlank(message = "Số giấy phép hành nghề không được để trống")
  private String licenseNo;

  @NotNull(message = "Ngày cấp giấy phép hành nghề không được để trống")
  private Date licenseIssueDate;

  @NotBlank(message = "Nơi cấp giấy phép hành nghề không được để trống")
  private String licenseIssuePlace;

  private String workplace; // co so lam viec (optional)

  // File URLs - optional, set by controller when files are uploaded
  private String practiceCertificateUrl;
  private String licenseUrl;
  private String nationalIdUrl;
}
