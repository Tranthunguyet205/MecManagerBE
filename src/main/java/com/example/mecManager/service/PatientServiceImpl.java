package com.example.mecManager.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mecManager.dto.PatientCreateDTO;
import com.example.mecManager.model.PatientProfile;
import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.User;
import com.example.mecManager.model.UserPrincipal;
import com.example.mecManager.repository.PatientProfileRepository;
import com.example.mecManager.repository.PrescriptionRepository;
import com.example.mecManager.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for Patient Profile management
 * Handles patient CRUD operations with proper DTO mapping and transaction
 * boundaries
 * Note: Audit fields (createdBy, updatedBy) are extracted from SecurityContext
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientProfileRepository patientProfileRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;

    /**
     * Retrieve a patient by ID
     * 
     * @param patientId the patient ID
     * @return PatientProfile DTO containing patient information
     * @throws EntityNotFoundException if patient not found
     */
    @Override
    @Transactional(readOnly = true)
    public PatientProfile getPatientById(Long patientId) {
        return patientProfileRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.warn("Patient not found with ID: {}", patientId);
                    return new EntityNotFoundException("Không tìm thấy bệnh nhân với ID: " + patientId);
                });
    }

    /**
     * Retrieve all patients with pagination and sorting
     * 
     * @param page     zero-based page number (default 0)
     * @param pageSize number of records per page (default 10)
     * @return Map containing paginated patients list and metadata
     */
    @Override
    @Transactional(readOnly = true)
    public Object getAllPatients(Integer page, Integer pageSize) {
        // Default values: page 0, size 10
        int currentPage = (page != null && page >= 0) ? page : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        try {
            // Create pageable with sorting by created date descending
            Pageable pageable = PageRequest.of(currentPage, size, Sort.by("createdAt").descending());
            Page<PatientProfile> patientPage = patientProfileRepository.findAll(pageable);

            // Build response with pagination metadata (Spring Data Convention)
            Map<String, Object> response = new HashMap<>();
            response.put("content", patientPage.getContent());
            response.put("page", patientPage.getNumber());
            response.put("size", patientPage.getSize());
            response.put("totalElements", patientPage.getTotalElements());
            response.put("totalPages", patientPage.getTotalPages());
            response.put("isFirst", patientPage.isFirst());
            response.put("isLast", patientPage.isLast());

            return response;

        } catch (Exception e) {
            log.error("Error retrieving patients list", e);
            throw new RuntimeException("Lỗi khi lấy danh sách bệnh nhân: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new patient profile from DTO
     * Maps DTO to entity, validates, sets audit fields, and saves
     * 
     * @param patientDTO the patient data DTO with validation
     * @return created PatientProfile
     * @throws RuntimeException if validation or database operation fails
     */
    @Override
    public PatientProfile createPatient(PatientCreateDTO patientDTO) {
        try {
            log.info("Starting patient creation with data: fullName={}, diagnosis={}",
                    patientDTO.getFullName(), patientDTO.getDiagnosis());

            // Get current user from SecurityContext with fallback to username lookup
            Long createdBy = getCurrentUserId();
            log.info("Current user ID obtained: {}", createdBy);

            User creator = userRepository.findById(createdBy)
                    .orElseThrow(() -> {
                        log.error("User not found with ID: {} after successful ID extraction", createdBy);
                        return new EntityNotFoundException("Không tìm thấy người dùng với ID: " + createdBy);
                    });

            log.info("User found: {} (ID: {})", creator.getUsername(), creator.getId());

            // Map DTO to entity
            PatientProfile patient = PatientProfile.builder()
                    .fullName(patientDTO.getFullName())
                    .dob(patientDTO.getDob())
                    .gender(patientDTO.getGender())
                    .address(patientDTO.getAddress())
                    .phone(patientDTO.getPhone())
                    .diagnosis(patientDTO.getDiagnosis())
                    .treatmentType(patientDTO.getTreatmentType())
                    .note(patientDTO.getNote())
                    .heightCm(patientDTO.getHeightCm())
                    .weightKg(patientDTO.getWeightKg())
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .createdBy(creator)
                    .updatedBy(creator)
                    .build();

            PatientProfile saved = patientProfileRepository.save(patient);
            log.info("Patient created successfully with ID: {}, created by: {}", saved.getId(), creator.getUsername());

            return saved;

        } catch (EntityNotFoundException e) {
            log.warn("Failed to create patient - entity not found: {}", e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating patient", e);
            // Parse database constraint errors into user-friendly messages
            String message = parseDatabaseError(e.getMessage());
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Update an existing patient profile
     * 
     * @param id      the patient ID
     * @param patient the updated patient data
     * @return updated PatientProfile
     * @throws EntityNotFoundException if patient not found
     */
    @Override
    public PatientProfile updatePatient(Long id, PatientProfile patient) {
        try {
            // Get current user from SecurityContext
            Long updatedBy = getCurrentUserId();
            User updater = userRepository.findById(updatedBy)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng hiện tại"));

            // Find patient to update
            PatientProfile existing = patientProfileRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Patient not found with ID: {}", id);
                        return new EntityNotFoundException("Không tìm thấy bệnh nhân với ID: " + id);
                    });

            // Update fields
            existing.setFullName(patient.getFullName());
            existing.setDob(patient.getDob());
            existing.setGender(patient.getGender());
            existing.setAddress(patient.getAddress());
            existing.setPhone(patient.getPhone());
            existing.setNote(patient.getNote());
            existing.setTreatmentType(patient.getTreatmentType());
            existing.setHeightCm(patient.getHeightCm());
            existing.setWeightKg(patient.getWeightKg());
            existing.setDiagnosis(patient.getDiagnosis());
            existing.setConclusion(patient.getConclusion());
            existing.setUpdatedAt(new Date());
            existing.setUpdatedBy(updater);

            PatientProfile updated = patientProfileRepository.save(existing);
            log.info("Patient updated successfully with ID: {}", updated.getId());

            return updated;

        } catch (EntityNotFoundException e) {
            log.warn("Failed to update patient: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating patient", e);
            String message = parseDatabaseError(e.getMessage());
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Delete a patient profile if no associated prescriptions exist
     * 
     * @param patientId the patient ID
     * @throws EntityNotFoundException if patient not found
     * @throws IllegalStateException   if patient has active prescriptions
     */
    @Override
    public void deletePatient(Long patientId) {
        try {
            // Find patient to delete
            PatientProfile patient = patientProfileRepository.findById(patientId)
                    .orElseThrow(() -> {
                        log.warn("Patient not found with ID: {}", patientId);
                        return new EntityNotFoundException("Không tìm thấy bệnh nhân với ID: " + patientId);
                    });

            // Check if patient has any prescriptions
            List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);

            if (!prescriptions.isEmpty()) {
                log.warn("Cannot delete patient {} - has {} prescriptions", patientId, prescriptions.size());
                throw new IllegalStateException(
                        "Không thể xóa bệnh nhân. Bệnh nhân có " + prescriptions.size() + " đơn thuốc đã được kê");
            }

            // Delete the patient
            patientProfileRepository.delete(patient);
            log.info("Patient deleted successfully with ID: {}", patientId);

        } catch (EntityNotFoundException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting patient", e);
            throw new RuntimeException("Lỗi khi xóa bệnh nhân: " + e.getMessage(), e);
        }
    }

    /**
     * Extract current user ID from SecurityContext
     * Uses username lookup as primary method since JWT sub claim may be outdated
     * Falls back to UserPrincipal ID if username lookup fails
     * 
     * @return current user ID
     * @throws RuntimeException if no user is authenticated
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.error("No authentication found in SecurityContext");
            throw new RuntimeException("Không tìm thấy người dùng được xác thực");
        }

        Object principal = auth.getPrincipal();
        log.info("Principal type: {}, value: {}", principal.getClass().getName(), principal);

        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            String username = userPrincipal.getUsername();
            Long idFromJwt = userPrincipal.getId();

            log.info("UserPrincipal - username: {}, id from JWT: {}", username, idFromJwt);

            // Primary method: find by username (most reliable)
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                log.info("Found user by username '{}', database ID: {}", username, user.getId());
                return user.getId();
            }

            log.warn("User not found by username: {}, attempting fallback to JWT ID", username);

            // Fallback: try ID from UserPrincipal
            if (idFromJwt != null && idFromJwt > 0) {
                log.info("Using ID from JWT/UserPrincipal: {}", idFromJwt);
                return idFromJwt;
            }

            log.error("Both username lookup and UserPrincipal ID failed for user: {}", username);
            throw new EntityNotFoundException("Không tìm thấy người dùng: " + username);
        }

        log.error("Principal is not UserPrincipal: {}", principal.getClass().getName());
        throw new RuntimeException("Không thể xác định người dùng hiện tại");
    }

    /**
     * Parse database error messages into user-friendly Vietnamese messages
     * 
     * @param errorMessage Raw database error message
     * @return User-friendly error message in Vietnamese
     */
    private String parseDatabaseError(String errorMessage) {
        if (errorMessage == null) {
            return "Lỗi khi lưu dữ liệu bệnh nhân";
        }

        // Parse constraint violations
        if (errorMessage.contains("Duplicate entry") || errorMessage.contains("unique")) {
            return "Dữ liệu bệnh nhân đã tồn tại";
        }
        if (errorMessage.contains("NOT NULL") || errorMessage.contains("cannot be null")) {
            return "Một số trường bắt buộc chưa được điền";
        }
        if (errorMessage.contains("Data too long")) {
            return "Dữ liệu nhập vào quá dài";
        }
        if (errorMessage.contains("out of range")) {
            return "Giá trị không hợp lệ";
        }

        // Fallback
        return "Lỗi khi lưu dữ liệu bệnh nhân. Vui lòng kiểm tra lại thông tin";
    }
}
