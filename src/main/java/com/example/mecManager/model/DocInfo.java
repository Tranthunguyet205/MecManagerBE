package com.example.mecManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "doctor_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false, unique = true, name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "dob", nullable = false)
    private Date dob; // ngay sinh

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "cccd", nullable = false)
    private String cccd;

    @Column(name = "cccd_issue_date", nullable = false)
    private Date cccdIssueDate;//ngay cap cccd

    @Column(name = "cccd_issue_place",nullable = false)
    private String cccdIssuePlace;

    @Column(name = "current_address", nullable = false)
    private String currentAddress;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "practice_certificate_no", nullable = false)
    private String practiceCertificateNo;// so chung chi hanh nghe

    @Column(name = "practice_certificate_issue_date", nullable = false)
    private Date practiceCertificateIssueDate;// ngay cap chung chi hanh nghe

    @Column(name = "practice_certificate_issue_place", nullable = false)
    private String practiceCertificateIssuePlace;// noi cap chung chi hanh nghe

    @Column(name = "license_no", nullable = false)
    private String licenseNo; // so giay phep hanh nghe

    @Column(name = "license_issue_date", nullable = false)
    private Date licenseIssueDate;// ngay cap giay phep hanh nghe

    @Column(name = "license_issue_place", nullable = false)
    private String licenseIssuePlace;// noi cap giay phep hanh nghe

    @Column(name = "created_at", nullable = false)
    private Date createdAt; // ngay tao ban ghi

    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "create_by", nullable = false)
    private User userCreateBy;

    @ManyToOne
    @JoinColumn(name = "update_by")
    private User userUpdateBy;

}
