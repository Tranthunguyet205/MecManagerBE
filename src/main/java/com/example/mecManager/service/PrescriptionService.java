package com.example.mecManager.service;

import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.PrescriptionDTO;
import com.example.mecManager.model.PrescriptionUpdateDTO;

import jakarta.persistence.EntityNotFoundException;

/**
 * Service interface for Prescription management
 * Handles prescription CRUD operations, search, and business logic
 */
public interface PrescriptionService {

    /**
     * Get prescription by code
     * 
     * @param code prescription code
     * @return Prescription entity
     * @throws EntityNotFoundException if not found
     */
    Prescription getPrescriptionByCode(String code);

    /**
     * Get prescription by ID
     * 
     * @param id prescription ID
     * @return Prescription entity
     * @throws EntityNotFoundException if not found
     */
    Prescription getPrescriptionById(Long id);

    /**
     * Search prescriptions by DTO criteria
     * 
     * @param prescriptionDTO search criteria
     * @return search result object with pagination and list
     */
    Object findByDTO(PrescriptionDTO prescriptionDTO);

    /**
     * Create a new prescription
     * 
     * @param prescriptionDTO prescription data
     * @return created Prescription entity
     */
    Prescription createPrescription(PrescriptionDTO prescriptionDTO);

    /**
     * Update prescription (must be within 1 hour of creation)
     * 
     * @param id                    prescription ID
     * @param prescriptionUpdateDTO updated data
     * @return updated Prescription entity
     * @throws IllegalStateException if prescription is older than 1 hour
     */
    Prescription updatePrescription(Long id, PrescriptionUpdateDTO prescriptionUpdateDTO);

    /**
     * Delete prescription
     * 
     * @param prescriptionId prescription ID
     */
    void deletePrescription(Long prescriptionId);
}
