package com.example.mecManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Response DTO for Medicine information
 * Excludes sensitive User data - only returns timestamps for audit trail
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponseDTO {
    
    private Long id;
    
    private String medicineCode;
    
    private String medicineName;
    
    private String ingredient;
    
    private Long quantity;
    
    private String usageInstructions;
    
    private Date createdAt;
    
    private Date updatedAt;
}
