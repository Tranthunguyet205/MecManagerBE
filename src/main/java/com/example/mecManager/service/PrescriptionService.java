package com.example.mecManager.service;

import com.example.mecManager.model.entity.Prescription;
import com.example.mecManager.dto.PrescriptionCreateDTO;
import com.example.mecManager.dto.PrescriptionUpdateDTO;

import jakarta.persistence.EntityNotFoundException;

public interface PrescriptionService {

    Prescription getPrescriptionByCode(String code);

    Prescription getPrescriptionById(Long id);

    Object findByDTO(PrescriptionCreateDTO prescriptionDTO);

    Prescription createPrescription(PrescriptionCreateDTO prescriptionDTO);

    Prescription updatePrescription(Long id, PrescriptionUpdateDTO prescriptionUpdateDTO);

    void deletePrescription(Long prescriptionId);
}
