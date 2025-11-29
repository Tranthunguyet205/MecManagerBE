package com.example.mecManager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mecManager.Common.ApiResponse;
import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.DocInfoDTO;
import com.example.mecManager.service.DoctorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AppConstants.URL.API_BASE + "/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Create a new doctor profile
     * 
     * @param docInfoDTO Doctor information DTO
     * @return ApiResponse with created doctor info
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<DocInfoDTO>> createDoctor(
            @Valid @RequestBody DocInfoDTO docInfoDTO) {
        try {
            DocInfoDTO createdDoctor = doctorService.createDoctor(docInfoDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Bác sĩ được tạo thành công", createdDoctor));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * Get doctor by ID
     * 
     * @param id Doctor ID
     * @return ApiResponse with doctor info
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocInfoDTO>> getDoctorById(@PathVariable Long id) {
        try {
            DocInfoDTO doctor = doctorService.getDoctorById(id);
            return ResponseEntity.ok(ApiResponse.success(doctor));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * Get all doctors with pagination
     * 
     * @param page     Page number (0-based, default: 0)
     * @param pageSize Number of items per page (default: 10)
     * @return ApiResponse with list of doctors
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllDoctors(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            var result = doctorService.getAllDoctors(page, pageSize);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    /**
     * Search doctors by criteria
     * 
     * @param practiceCertificateNo Practice certificate number (optional)
     * @param licenseNo             License number (optional)
     * @param page                  Page number (0-based)
     * @param pageSize              Items per page
     * @return ApiResponse with search results
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchDoctors(
            @RequestParam(required = false) String practiceCertificateNo,
            @RequestParam(required = false) String licenseNo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            var result = doctorService.searchDoctors(practiceCertificateNo, licenseNo, page, pageSize);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * Update doctor information
     * 
     * @param id         Doctor ID
     * @param docInfoDTO Updated doctor information
     * @return ApiResponse with updated doctor info
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<DocInfoDTO>> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DocInfoDTO docInfoDTO) {
        try {
            DocInfoDTO updatedDoctor = doctorService.updateDoctor(id, docInfoDTO);
            return ResponseEntity.ok(ApiResponse.success("Bác sĩ được cập nhật thành công", updatedDoctor));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * Delete doctor by ID
     * 
     * @param id Doctor ID
     * @return ApiResponse with deletion status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable Long id) {
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.ok(ApiResponse.successMessage("Bác sĩ được xóa thành công"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }
}
