package com.example.mecManager.model;

import lombok.Data;

@Data
public class PrescriptionDTO {
    // ===== INPUT FILTER =====
    private String prescriptionCode;
    private Integer treatmentType;
    private String createDateTime;
    private Integer page;
    private Integer pageSize;

    // ===== OUTPUT FIELDS =====
    private String address;
    private String patientName;
    private String preCode;
    private Long total;
}
