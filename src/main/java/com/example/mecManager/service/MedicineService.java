package com.example.mecManager.service;

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
     * @return MedicineResponseDTO
     * @throws EntityNotFoundException if not found
     */
    Object getMedicineById(Long id);

    /**
     * Get all medicines with pagination
     * 
     * @param page     zero-based page number (default 0)
     * @param pageSize number of records per page (default 10)
     * @return Map containing paginated medicines list and metadata
     */
    Object getAllMedicines(Integer page, Integer pageSize);

    /**
     * Search medicines by name or code
     * 
     * @param query    Search query (medicine name or code)
     * @param page     zero-based page number (default 0)
     * @param pageSize number of records per page (default 10)
     * @return Map containing paginated search results and metadata
     */
    Object searchMedicines(String query, Integer page, Integer pageSize);

    /**
     * Create a new medicine
     * 
     * @param medicineDTO medicine data
     * @return created MedicineResponseDTO
     */
    Object createMedicine(MedicineInfoDTO medicineDTO);

    /**
     * Update medicine information
     * 
     * @param id          medicine ID
     * @param medicineDTO updated medicine data
     * @return updated MedicineResponseDTO
     */
    Object updateMedicine(Long id, MedicineInfoDTO medicineDTO);

    /**
     * Delete medicine by ID
     * 
     * @param id medicine ID
     */
    void deleteMedicine(Long id);
}
