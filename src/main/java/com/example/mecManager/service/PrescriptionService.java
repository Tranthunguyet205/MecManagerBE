package com.example.mecManager.service;

import com.example.mecManager.model.PrescriptionDTO;
import com.example.mecManager.model.PrescriptionUpdateDTO;
import com.example.mecManager.model.ResponseObject;

public interface PrescriptionService {
    ResponseObject getPrescriptionByCode(String code);
    ResponseObject getPrescriptionById(Long id);
    ResponseObject findByDTO(PrescriptionDTO prescriptionDTO);
    ResponseObject updatePrescription(PrescriptionUpdateDTO prescriptionUpdateDTO, Long updatedBy);
    ResponseObject deletePrescription(Long prescriptionId, Long deletedBy);
}
