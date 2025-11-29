package com.example.mecManager.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patient_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    @Column(name = "dob", nullable = false)
    private Date dob;

    @Column(name = "gender", nullable = false)
    private Integer gender;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone", nullable = true)
    private String phone;

    @Column(name = "note", nullable = true)
    private String note;

    @Column(name = "treatment_type", nullable = false)
    private Integer treatmentType;// loai dieu tri 0: Ngoại trú, 1: Nội trú

    @Column(name = "height_cm", nullable = true)
    private Float heightCm;

    @Column(name = "weight_kg", nullable = true)
    private Float weightKg;

    @Column(name = "diagnosis", nullable = false)
    private String diagnosis;// chuan doan benh

    @Column(name = "conclusion")
    private String conclusion;// ket luan benh

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnore
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private User updatedBy;

}
