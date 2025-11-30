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
public class DocInfoDTO {
  private Long id;
  private Long userId;
  private String fullName;
  private Date dob;
  private String phone;
  private String cccd;
  private Date cccdIssueDate;
  private String cccdIssuePlace;
  private String currentAddress;
  private String email;
  private String practiceCertificateNo;
  private Date practiceCertificateIssueDate;
  private String practiceCertificateIssuePlace;
  private String licenseNo;
  private Date licenseIssueDate;
  private String licenseIssuePlace;
  private String practiceCertificateUrl;
  private String licenseUrl;
  private String nationalIdUrl;
  private Date createdAt;
  private Date updatedAt;
}
