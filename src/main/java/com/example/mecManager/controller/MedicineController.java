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

import com.example.mecManager.common.response.ApiResponse;
import com.example.mecManager.common.constants.AppConstants;
import com.example.mecManager.dto.MedicineInfoDTO;
import com.example.mecManager.service.MedicineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AppConstants.URL.API_BASE + "/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    /**
     * Create a new medicine
     *
     * @param dto Medicine data
     * @return ApiResponse with created medicine
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> createMedicine(
            @Valid @RequestBody MedicineInfoDTO dto) {
        try {
            var medicine = medicineService.createMedicine(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Thuốc được tạo thành công", medicine));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * Get medicine by ID
     *
     * @param id Medicine ID
     * @return ApiResponse with medicine details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getMedicineById(@PathVariable Long id) {
        try {
            var medicine = medicineService.getMedicineById(id);
            return ResponseEntity.ok(ApiResponse.success(medicine));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * Get all medicines with pagination
     *
     * @param page     Page number (0-based)
     * @param pageSize Items per page
     * @return ApiResponse with medicine list
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllMedicines(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            var medicines = medicineService.getAllMedicines(page, pageSize);
            return ResponseEntity.ok(ApiResponse.success(medicines));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    /**
     * Update medicine
     *
     * @param id  Medicine ID
     * @param dto Updated medicine data
     * @return ApiResponse with updated medicine
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateMedicine(
            @PathVariable Long id, @Valid @RequestBody MedicineInfoDTO dto) {
        try {
            var updated = medicineService.updateMedicine(id, dto);
            return ResponseEntity.ok(ApiResponse.success("Thuốc được cập nhật thành công", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * Delete medicine
     * Note: Medicines with associated prescription details cannot be deleted
     *
     * @param id Medicine ID
     * @return ApiResponse with deletion status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMedicine(@PathVariable Long id) {
        try {
            medicineService.deleteMedicine(id);
            return ResponseEntity.ok(ApiResponse.successMessage("Thuốc được xóa thành công"));
        } catch (IllegalStateException e) {
            // Medicine is in use
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(409, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, e.getMessage()));
        }
    }
}