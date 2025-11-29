package com.example.mecManager.service;

import com.example.mecManager.dto.PatientCreateDTO;
import com.example.mecManager.model.PatientProfile;

public interface PatientService {
  /**
   * Get patient by ID
   *
   * @param id Patient ID
   * @return Patient profile
   */
  PatientProfile getPatientById(Long id);

  /**
   * Get all patients with pagination
   *
   * @param page     Page number (0-based)
   * @param pageSize Items per page
   * @return Paginated result
   */
  Object getAllPatients(Integer page, Integer pageSize);

  /**
   * Create a new patient
   *
   * @param patientDTO Patient data (DTO with validation)
   * @return Created patient
   */
  PatientProfile createPatient(PatientCreateDTO patientDTO);

  /**
   * Update patient information
   *
   * @param id      Patient ID
   * @param patient Updated patient data
   * @return Updated patient
   */
  PatientProfile updatePatient(Long id, PatientProfile patient);

  /**
   * Delete patient by ID
   *
   * @param id Patient ID
   */
  void deletePatient(Long id);
}
