package com.example.mecManager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocInfoDTO {
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
}
