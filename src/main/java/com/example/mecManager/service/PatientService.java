package com.example.mecManager.service;

import com.example.mecManager.model.ResponseObject;

public interface PatientService {
    ResponseObject getPatientById(Long patientId);
    ResponseObject getAllPatients(Integer page, Integer pageSize);
    ResponseObject deletePatient(Long patientId, Long deletedBy);
}
