package com.example.mecManager.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import java.util.Date;

@Entity
@Table(name = "medicine_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_code", nullable = false)
    private String medicineCode;// ma thuoc

    @Column(name = "ingredient", nullable = false)
    private String ingredient;// thanh phan thuoc

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;// ten thuoc

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "usage_instructions", nullable = false)
    private String usageInstructions;// cach dung thuoc

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
