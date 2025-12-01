package com.example.mecManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionResponseDTO {
    
    private Long id;
    private String prescriptionCode;
    
    // Patient embedded fields
    private String patientName;
    private String patientNationalId;
    
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date patientDob;
    
    private Integer patientGender;
    private String patientAddress;
    private String patientPhone;
    
    // Medical info
    private String diagnosis;
    private String conclusion;
    private Integer treatmentType;
    private Float heightCm;
    private Float weightKg;
    
    private Long doctorId;
    private String doctorName;
    
    private String note;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date updatedAt;
    
    private List<PrescriptionMedicineDTO> medicines;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrescriptionMedicineDTO {
        
        private Long medicineId;
        private String medicineCode;
        private String medicineName;
        private String ingredient;
        
        private Integer quantity;
        private String usageInstructions;
    }
}
