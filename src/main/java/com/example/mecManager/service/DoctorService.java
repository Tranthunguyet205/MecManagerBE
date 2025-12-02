package com.example.mecManager.service;

import com.example.mecManager.dto.DocInfoDTO;
import com.example.mecManager.dto.DocInfoUpdateDTO;

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
     * Get doctors with optional search filters
     *
     * @param search                Search term (searches name and email)
     * @param practiceCertificateNo Practice certificate number (optional)
     * @param licenseNo             License number (optional)
     * @param page                  Page number (0-based)
     * @param pageSize              Items per page
     * @return Paginated result with optional filter applied
     */
    Object getDoctors(
            String search, String practiceCertificateNo, String licenseNo, Integer page, Integer pageSize);

    /**
     * Update doctor information
     *
     * @param id               Doctor ID
     * @param docInfoUpdateDTO Updated doctor info (without userId)
     * @return Updated doctor DTO
     */
    DocInfoDTO updateDoctor(Long id, DocInfoUpdateDTO docInfoUpdateDTO);

    /**
     * Delete doctor by ID
     *
     * @param id Doctor ID
     */
    void deleteDoctor(Long id);
}
