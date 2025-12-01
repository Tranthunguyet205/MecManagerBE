package com.example.mecManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionUpdateDTO {
    private Long prescriptionId;
    private Long patientId;
    private Long doctorId;
    private String note;
    private List<PrescriptionDetailDTO> prescriptionDetails;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionDetailDTO {
        private Long medicineId;
        private Integer quantity;
        private String usageInstructions;
    }
}
