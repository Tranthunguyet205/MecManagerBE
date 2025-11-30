package com.example.mecManager.controller;

import org.hibernate.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.mecManager.Common.ApiResponse;
import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.DocInfoDTO;
import com.example.mecManager.model.DocInfoUpdateDTO;
import com.example.mecManager.service.DoctorService;
import com.example.mecManager.service.FileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AppConstants.URL.API_BASE + "/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final FileService fileService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create new doctor profile", description = "Create a new doctor profile. Admin access required.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Doctor created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<DocInfoDTO>> createDoctor(
            @Valid @RequestBody DocInfoDTO docInfoDTO) {
        try {
            DocInfoDTO createdDoctor = doctorService.createDoctor(docInfoDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Doctor created successfully", createdDoctor));
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
    @Operation(summary = "Get doctor by ID", description = "Retrieve doctor information by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctor found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found")
    })
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
    @Operation(summary = "Get all doctors", description = "Retrieve all doctors with pagination support")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctors retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "System error")
    })
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
    @Operation(summary = "Search doctors", description = "Search doctors by practice certificate number or license number")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found")
    })
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
     * Update doctor information with file uploads
     * 
     * @param id               Doctor ID
     * @param docInfoUpdateDTO Updated doctor information (without userId)
     * @param practiceCert     Practice certificate file
     * @param license          License file
     * @param nationalId       National ID file
     * @return ApiResponse with updated doctor info
     */
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update doctor profile", description = "Update doctor information with optional file uploads. Admin access required.", parameters = {
            @Parameter(name = "id", description = "Doctor ID to update", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", example = "1"))
    })
    @RequestBody(description = "Doctor update data with optional files", required = true, content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "object")))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or file upload failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<ApiResponse<DocInfoDTO>> updateDoctor(
            @Parameter(description = "Doctor ID to update", required = true, example = "1") @PathVariable Long id,
            @RequestPart("doctorInfo") @Valid DocInfoUpdateDTO docInfoUpdateDTO,
            @Parameter(description = "Practice certificate file (optional, max 5MB)") @RequestPart(value = "practiceCertificate", required = false) MultipartFile practiceCert,
            @Parameter(description = "License file (optional, max 5MB)") @RequestPart(value = "license", required = false) MultipartFile license,
            @Parameter(description = "National ID file (optional, max 5MB)") @RequestPart(value = "nationalId", required = false) MultipartFile nationalId) {
        try {
            // Upload files if provided
            if (practiceCert != null && !practiceCert.isEmpty()) {
                String url = fileService.uploadFile("Doctor", id,
                        "practice-cert-" + System.currentTimeMillis() + getExtension(practiceCert), practiceCert);
                docInfoUpdateDTO.setPracticeCertificateUrl(url);
            }
            if (license != null && !license.isEmpty()) {
                String url = fileService.uploadFile("Doctor", id,
                        "license-" + System.currentTimeMillis() + getExtension(license), license);
                docInfoUpdateDTO.setLicenseUrl(url);
            }
            if (nationalId != null && !nationalId.isEmpty()) {
                String url = fileService.uploadFile("Doctor", id,
                        "national-id-" + System.currentTimeMillis() + getExtension(nationalId), nationalId);
                docInfoUpdateDTO.setNationalIdUrl(url);
            }

            DocInfoDTO updatedDoctor = doctorService.updateDoctor(id, docInfoUpdateDTO);
            return ResponseEntity.ok(ApiResponse.success("Doctor updated successfully", updatedDoctor));
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
    @Operation(summary = "Delete doctor", description = "Delete doctor profile and associated files. Admin access required.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctor deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable Long id) {
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.ok(ApiResponse.successMessage("Doctor deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    private String getExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.contains("."))
            return "";
        return name.substring(name.lastIndexOf("."));
    }
}
