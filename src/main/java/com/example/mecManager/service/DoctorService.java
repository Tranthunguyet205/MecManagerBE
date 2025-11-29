package com.example.mecManager.service;

import com.example.mecManager.model.DocInfoDTO;

public interface DoctorService {
    /**
     * Create a new doctor profile
     *
     * @param docInfoDTO Doctor information
     * @return Created doctor DTO
     */
    DocInfoDTO createDoctor(DocInfoDTO docInfoDTO);

    /**
     * Get doctor by ID
     *
     * @param id Doctor ID
     * @return Doctor DTO
     */
    DocInfoDTO getDoctorById(Long id);

    /**
     * Get all doctors with pagination
     *
     * @param page     Page number (0-based)
     * @param pageSize Items per page
     * @return Paginated result
     */
    Object getAllDoctors(Integer page, Integer pageSize);

    /**
     * Search doctors by criteria
     *
     * @param practiceCertificateNo Practice certificate number
     * @param licenseNo             License number
     * @param page                  Page number
     * @param pageSize              Items per page
     * @return Search results
     */
    Object searchDoctors(
            String practiceCertificateNo, String licenseNo, Integer page, Integer pageSize);

    /**
     * Update doctor information
     *
     * @param id         Doctor ID
     * @param docInfoDTO Updated doctor info
     * @return Updated doctor DTO
     */
    DocInfoDTO updateDoctor(Long id, DocInfoDTO docInfoDTO);

    /**
     * Delete doctor by ID
     *
     * @param id Doctor ID
     */
    void deleteDoctor(Long id);
}
