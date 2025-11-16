package com.example.mecManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import java.util.Date;

@Entity
@Table(name = "patient_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "dob", nullable = false)
    private Date dob;

    @Column(name = "gender", nullable = false)
    private Integer gender;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "note", nullable = false)
    private String note;

    @Column(name = "treatment_type", nullable = false)
    private Integer treatmentType;// loai dieu tri 0: Ngoại trú, 1: Nội trú

    @Column(name = "height_cm", nullable = false)
    private float heightCm;

    @Column(name = "weight_kg", nullable = false)
    private float weightKg;

    @Column(name = "diagnosis", nullable = false)
    private String diagnosis;// chuan doan benh

    @Column(name = "conclusion")
    private String conclusion;// ket luan benh

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "create_by", nullable = false)
    private User userCreateBy;

    @ManyToOne
    @JoinColumn(name = "update_by")
    private User userUpdateBy;

}
