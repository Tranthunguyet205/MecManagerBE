package com.example.mecManager.service;

import com.example.mecManager.model.entity.MedicineInfo;
import com.example.mecManager.dto.MedicineInfoDTO;

import jakarta.persistence.EntityNotFoundException;

/**
 * Service interface for Medicine management
 * Handles medicine CRUD operations and pharmacy data
 */
public interface MedicineService {

    /**
     * Get medicine by ID
     * 
     * @param id medicine ID
     * @return MedicineInfo entity
     * @throws EntityNotFoundException if not found
     */
    MedicineInfo getMedicineById(Long id);

    /**
     * Get all medicines with pagination
     * 
     * @param page     zero-based page number (default 0)
     * @param pageSize number of records per page (default 10)
     * @return Map containing paginated medicines list and metadata
     */
    Object getAllMedicines(Integer page, Integer pageSize);

    /**
     * Create a new medicine
     * 
     * @param medicineDTO medicine data
     * @return created MedicineInfo entity
     */
    MedicineInfo createMedicine(MedicineInfoDTO medicineDTO);

    /**
     * Update medicine information
     * 
     * @param id          medicine ID
     * @param medicineDTO updated medicine data
     * @return updated MedicineInfo entity
     */
    MedicineInfo updateMedicine(Long id, MedicineInfoDTO medicineDTO);

    /**
     * Delete medicine by ID
     * 
     * @param id medicine ID
     */
    void deleteMedicine(Long id);
}
