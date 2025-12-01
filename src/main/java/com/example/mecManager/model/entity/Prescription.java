package com.example.mecManager.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_code", nullable = false, unique = true)
    private String prescriptionCode;

    // Patient embedded fields
    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @Column(name = "patient_national_id", nullable = false)
    private String patientNationalId;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    @Column(name = "patient_dob", nullable = false)
    private Date patientDob;

    @Column(name = "patient_gender", nullable = false)
    private Integer patientGender;

    @Column(name = "patient_address", nullable = false)
    private String patientAddress;

    @Column(name = "patient_phone")
    private String patientPhone;

    // Medical info
    @Column(name = "diagnosis", nullable = false)
    private String diagnosis;

    @Column(name = "conclusion")
    private String conclusion;

    @Column(name = "treatment_type", nullable = false)
    private Integer treatmentType;

    @Column(name = "height_cm")
    private Float heightCm;

    @Column(name = "weight_kg")
    private Float weightKg;

    @Column(name = "note")
    private String note;

    // Doctor reference
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DocInfo docInfo;

    // Prescription medicines
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<PrescriptionDetail> medicines;

    // Audit fields
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
