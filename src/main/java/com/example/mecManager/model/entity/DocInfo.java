package com.example.mecManager.model.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctor_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private Date cccdIssueDate;// ngay cap cccd

    @Column(name = "cccd_issue_place", nullable = false)
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

    @Column(name = "workplace")
    private String workplace; // co so lam viec

    @Column(name = "practice_certificate_url", nullable = true)
    private String practiceCertificateUrl;

    @Column(name = "license_url", nullable = true)
    private String licenseUrl;

    @Column(name = "national_id_url", nullable = true)
    private String nationalIdUrl;

    @Column(name = "created_at", nullable = false)
    private Date createdAt; // ngay tao ban ghi

    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "create_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "update_by")
    private User updatedBy;

}
