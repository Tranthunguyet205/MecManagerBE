package com.example.mecManager.dto;

import java.util.Date;

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

  private String fullName; // optional for update

  private Date dob; // optional for update

  private String phone; // optional for update

  private String cccd; // optional for update

  private Date cccdIssueDate; // optional for update

  private String cccdIssuePlace; // optional for update

  private String currentAddress; // optional for update

  private String email; // optional for update

  private String practiceCertificateNo; // optional for update

  private Date practiceCertificateIssueDate; // optional for update

  private String practiceCertificateIssuePlace; // optional for update

  private String licenseNo; // optional for update

  private Date licenseIssueDate; // optional for update

  private String licenseIssuePlace; // optional for update

  private String workplace; // co so lam viec (optional)

  private String professionalDegree; // van bang chuyen mon (optional)

  // File URLs - optional, set by controller when files are uploaded
  private String practiceCertificateUrl;
  private String licenseUrl;
  private String nationalIdUrl;
}
