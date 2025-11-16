package com.example.mecManager.repository;

import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.PrescriptionDTO;
import com.example.mecManager.model.PrescriptionResDTO;

import java.util.List;

public interface PrescriptionRepositoryJdbc {
    PrescriptionResDTO findPrescriptionsByDTO(PrescriptionDTO prescriptionDTO);
}
