package com.example.mecManager.model;

import lombok.Data;

import java.util.List;

@Data
public class PrescriptionResDTO {
    List<PrescriptionDTO> prescriptionDTO;
    private Long total;


}

