package com.example.mecManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineInfoDTO {
    private String medicineCode;
    private String ingredient;
    private String medicineName;
    private Long quantity;
    private String usageInstructions;
}
