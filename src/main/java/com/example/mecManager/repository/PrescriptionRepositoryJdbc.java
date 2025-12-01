package com.example.mecManager.repository;

import com.example.mecManager.model.entity.Prescription;
import com.example.mecManager.dto.PrescriptionDTO;
import com.example.mecManager.dto.PrescriptionResDTO;

import java.util.List;

public interface PrescriptionRepositoryJdbc {
    PrescriptionResDTO findPrescriptionsByDTO(PrescriptionDTO prescriptionDTO);
}
