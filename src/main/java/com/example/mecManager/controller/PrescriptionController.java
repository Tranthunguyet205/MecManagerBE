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
import com.example.mecManager.model.PrescriptionDTO;
import com.example.mecManager.model.PrescriptionUpdateDTO;
import com.example.mecManager.service.PrescriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AppConstants.URL.API_BASE + "/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

  private final PrescriptionService prescriptionService;

  /**
   * Create a new prescription
   *
   * @param dto Prescription data
   * @return ApiResponse with created prescription
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_ADMIN')")
  public ResponseEntity<ApiResponse<?>> createPrescription(@Valid @RequestBody PrescriptionDTO dto) {
    try {
      var prescription = prescriptionService.createPrescription(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(prescription));
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error(400, e.getMessage()));
    }
  }

  /**
   * Get prescription by ID
   *
   * @param id Prescription ID
   * @return ApiResponse with prescription details
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<?>> getPrescriptionById(@PathVariable Long id) {
    try {
      var prescription = prescriptionService.getPrescriptionById(id);
      return ResponseEntity.ok(ApiResponse.success(prescription));
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(404, e.getMessage()));
    }
  }

  /**
   * Get all prescriptions with pagination (via search with no criteria)
   *
   * @param page     Page number (0-based)
   * @param pageSize Items per page
   * @return ApiResponse with prescription list
   */
  @GetMapping
  public ResponseEntity<ApiResponse<?>> getAllPrescriptions(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    try {
      // Create empty search criteria to get all prescriptions
      PrescriptionDTO searchDTO = new PrescriptionDTO();
      searchDTO.setPage(page);
      searchDTO.setPageSize(pageSize);
      var prescriptions = prescriptionService.findByDTO(searchDTO);
      return ResponseEntity.ok(ApiResponse.success(prescriptions));
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(500, e.getMessage()));
    }
  }

  /**
   * Search prescriptions by criteria
   *
   * @param prescriptionCode Prescription code filter (optional)
   * @param patientId        Patient ID filter (optional)
   * @param doctorId         Doctor ID filter (optional)
   * @param treatmentType    Treatment type filter (optional, 0=outpatient,
   *                         1=inpatient)
   * @param createDateTime   Creation date filter (optional)
   * @param page             Page number (0-based)
   * @param pageSize         Items per page
   * @return ApiResponse with search results
   */
  @GetMapping("/search")
  public ResponseEntity<ApiResponse<?>> searchPrescriptions(
      @RequestParam(required = false) String prescriptionCode,
      @RequestParam(required = false) Long patientId,
      @RequestParam(required = false) Long doctorId,
      @RequestParam(required = false) Integer treatmentType,
      @RequestParam(required = false) String createDateTime,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    try {
      // Build DTO from query parameters
      PrescriptionDTO dto = new PrescriptionDTO();
      dto.setPrescriptionCode(prescriptionCode);
      dto.setPatientId(patientId);
      dto.setDoctorId(doctorId);
      dto.setTreatmentType(treatmentType);
      dto.setCreateDateTime(createDateTime);
      dto.setPage(page);
      dto.setPageSize(pageSize);

      var results = prescriptionService.findByDTO(dto);
      return ResponseEntity.ok(ApiResponse.success(results));
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error(400, e.getMessage()));
    }
  }

  /**
   * Update prescription
   *
   * @param id  Prescription ID
   * @param dto Updated prescription data
   * @return ApiResponse with updated prescription
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_ADMIN')")
  public ResponseEntity<ApiResponse<?>> updatePrescription(
      @PathVariable Long id, @Valid @RequestBody PrescriptionUpdateDTO dto) {
    try {
      var updated = prescriptionService.updatePrescription(id, dto);
      return ResponseEntity.ok(ApiResponse.success("Đơn thuốc được cập nhật thành công", updated));
    } catch (IllegalStateException e) {
      // Time limit exceeded
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error(400, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(404, e.getMessage()));
    }
  }

  /**
   * Delete prescription
   *
   * @param id Prescription ID
   * @return ApiResponse with deletion status
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deletePrescription(@PathVariable Long id) {
    try {
      prescriptionService.deletePrescription(id);
      return ResponseEntity.ok(ApiResponse.successMessage("Đơn thuốc được xóa thành công"));
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(404, e.getMessage()));
    }
  }
}
