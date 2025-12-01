package com.example.mecManager.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "prescription_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    @JsonBackReference
    private Prescription prescription;   // 1 chi tiết thuộc 1 đơn thuốc

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private MedicineInfo medicineInfo;   // 1 chi tiết chứa 1 loại thuốc

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "usage_instructions")
    private String usageInstructions;
}
