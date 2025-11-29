package com.example.mecManager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
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
import com.example.mecManager.dto.PatientCreateDTO;
import com.example.mecManager.dto.PatientResponseDTO;
import com.example.mecManager.model.PatientProfile;
import com.example.mecManager.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API Controller for Patient Profile Management
 * Endpoints for creating, reading, updating, and deleting patient records
 * All endpoints require authentication; write operations require ADMIN or
 * DOCTOR role
 */
@RestController
@RequestMapping(AppConstants.URL.API_BASE + "/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patient Management", description = "APIs for managing patient profiles")
public class PatientController {

    private final PatientService patientService;

    /**
     * Get patient by ID
     *
     * @param id Patient ID
     * @return ApiResponse with patient information
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieves a specific patient profile by their ID")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<?>> getPatientById(
            @Parameter(description = "Patient ID", required = true) @PathVariable Long id) {
        try {
            var patient = patientService.getPatientById(id);
            PatientResponseDTO response = convertToResponseDTO(patient);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * Get all patients with pagination
     *
     * @param page     Page number (0-based, default: 0)
     * @param pageSize Number of items per page (default: 10)
     * @return ApiResponse with list of patients
     */
    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieves a paginated list of all patients")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patients retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<?>> getAllPatients(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            var paginationData = patientService.getAllPatients(page, pageSize);

            // Convert pagination response
            if (paginationData instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) paginationData;
                @SuppressWarnings("unchecked")
                List<PatientProfile> content = (List<PatientProfile>) map.get("content");

                // Convert each patient to response DTO
                List<PatientResponseDTO> convertedContent = content.stream()
                        .map(this::convertToResponseDTO)
                        .toList();

                map.put("content", convertedContent);
            }

            return ResponseEntity.ok(ApiResponse.success(paginationData));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    /**
     * Create a new patient profile
     * Accepts PatientCreateDTO with validation
     *
     * @param patientDTO    Patient data with validation
     * @param bindingResult Validation results
     * @return ApiResponse with created patient or validation errors
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Create new patient", description = "Creates a new patient profile. Requires ADMIN or DOCTOR role")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<?>> createPatient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Patient data to create") @Valid @RequestBody PatientCreateDTO patientDTO,
            BindingResult bindingResult) {

        // Handle validation errors
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();
            log.warn("Validation failed for patient creation: {}", errors);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "Dữ liệu không hợp lệ", errors));
        }

        try {
            var created = patientService.createPatient(patientDTO);
            log.info("Patient created successfully with ID: {}", created.getId());

            // Convert to response DTO (only include user IDs, not full user objects)
            PatientResponseDTO response = PatientResponseDTO.builder()
                    .id(created.getId())
                    .fullName(created.getFullName())
                    .dob(created.getDob())
                    .gender(created.getGender())
                    .address(created.getAddress())
                    .phone(created.getPhone())
                    .note(created.getNote())
                    .treatmentType(created.getTreatmentType())
                    .heightCm(created.getHeightCm())
                    .weightKg(created.getWeightKg())
                    .diagnosis(created.getDiagnosis())
                    .conclusion(created.getConclusion())
                    .createdAt(created.getCreatedAt())
                    .updatedAt(created.getUpdatedAt())
                    .createdBy(created.getCreatedBy() != null ? created.getCreatedBy().getId() : null)
                    .updatedBy(created.getUpdatedBy() != null ? created.getUpdatedBy().getId() : null)
                    .build();

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Bệnh nhân được tạo thành công", response));
        } catch (RuntimeException e) {
            log.error("Error creating patient", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating patient", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống khi tạo bệnh nhân"));
        }
    }

    /**
     * Update patient information
     *
     * @param id      Patient ID
     * @param patient Updated patient data
     * @return ApiResponse with updated patient
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Update patient", description = "Updates an existing patient profile. Requires ADMIN or DOCTOR role")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiResponse<?>> updatePatient(
            @Parameter(description = "Patient ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated patient data") @Valid @RequestBody PatientProfile patient) {
        try {
            var updated = patientService.updatePatient(id, patient);
            PatientResponseDTO response = convertToResponseDTO(updated);
            return ResponseEntity.ok(ApiResponse.success("Bệnh nhân được cập nhật thành công", response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * Delete patient by ID
     * Note: Patients with associated prescriptions cannot be deleted
     *
     * @param id Patient ID
     * @return ApiResponse with deletion status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete patient", description = "Deletes a patient profile. Only ADMIN role. Cannot delete if patient has prescriptions")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - patient has associated prescriptions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @Parameter(description = "Patient ID", required = true) @PathVariable Long id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.ok(ApiResponse.successMessage("Bệnh nhân được xóa thành công"));
        } catch (IllegalStateException e) {
            // Patient has prescriptions
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(409, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * Helper method to convert PatientProfile entity to PatientResponseDTO
     * Only includes user IDs, not full user objects
     *
     * @param patient the patient entity
     * @return response DTO with user IDs
     */
    private PatientResponseDTO convertToResponseDTO(PatientProfile patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .dob(patient.getDob())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .phone(patient.getPhone())
                .note(patient.getNote())
                .treatmentType(patient.getTreatmentType())
                .heightCm(patient.getHeightCm())
                .weightKg(patient.getWeightKg())
                .diagnosis(patient.getDiagnosis())
                .conclusion(patient.getConclusion())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .createdBy(patient.getCreatedBy() != null ? patient.getCreatedBy().getId() : null)
                .updatedBy(patient.getUpdatedBy() != null ? patient.getUpdatedBy().getId() : null)
                .build();
    }
}
