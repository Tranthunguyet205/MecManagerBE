package com.example.mecManager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mecManager.model.DocInfo;
import com.example.mecManager.model.MedicineInfo;
import com.example.mecManager.model.PatientProfile;
import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.PrescriptionDTO;
import com.example.mecManager.model.PrescriptionDetail;
import com.example.mecManager.model.PrescriptionResDTO;
import com.example.mecManager.model.PrescriptionUpdateDTO;
import com.example.mecManager.model.UserPrincipal;
import com.example.mecManager.repository.DocInfoRepository;
import com.example.mecManager.repository.MedicineInfoRepository;
import com.example.mecManager.repository.PatientProfileRepository;
import com.example.mecManager.repository.PrescriptionDetailRepository;
import com.example.mecManager.repository.PrescriptionRepository;
import com.example.mecManager.repository.PrescriptionRepositoryJdbc;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for Prescription management
 * Handles prescription CRUD operations, search, and business logic
 * Important: Prescriptions can only be edited within 1 hour of creation
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionRepositoryJdbc prescriptionRepositoryJdbc;
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DocInfoRepository docInfoRepository;
    private final MedicineInfoRepository medicineInfoRepository;

    /**
     * Get prescription by code
     * 
     * @param code prescription code
     * @return Prescription entity
     * @throws EntityNotFoundException if not found
     */
    @Override
    @Transactional(readOnly = true)
    public Prescription getPrescriptionByCode(String code) {
        Prescription prescription = prescriptionRepository.findPrescriptionByPrescriptionCode(code);
        if (prescription == null) {
            log.warn("Prescription not found with code: {}", code);
            throw new EntityNotFoundException("Không tìm thấy đơn thuốc với mã: " + code);
        }
        return prescription;
    }

    /**
     * Get prescription by ID
     * 
     * @param id prescription ID
     * @return Prescription entity
     * @throws EntityNotFoundException if not found
     */
    @Override
    @Transactional(readOnly = true)
    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Prescription not found with ID: {}", id);
                    return new EntityNotFoundException("Không tìm thấy đơn thuốc với ID: " + id);
                });
    }

    /**
     * Search prescriptions by DTO criteria
     * 
     * @param prescriptionDTO search criteria
     * @return search result object with pagination and list
     */
    @Override
    @Transactional(readOnly = true)
    public Object findByDTO(PrescriptionDTO prescriptionDTO) {
        try {
            PrescriptionResDTO result = prescriptionRepositoryJdbc.findPrescriptionsByDTO(prescriptionDTO);
            log.debug("Prescription search completed with criteria: {}", prescriptionDTO);
            return result;
        } catch (Exception e) {
            log.error("Error searching prescriptions", e);
            throw new RuntimeException("Lỗi khi tìm kiếm đơn thuốc: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new prescription
     * 
     * @param prescriptionDTO prescription data
     * @return created Prescription entity
     */
    @Override
    public Prescription createPrescription(PrescriptionDTO prescriptionDTO) {
        try {
            // Get current user from SecurityContext (for audit purposes)
            getCurrentUserId();

            // Validate required fields
            if (prescriptionDTO.getPrescriptionCode() == null || prescriptionDTO.getPrescriptionCode().isEmpty()) {
                throw new IllegalArgumentException("Mã đơn thuốc không được để trống");
            }

            if (prescriptionDTO.getPatientId() == null) {
                throw new IllegalArgumentException("ID bệnh nhân không được để trống");
            }

            if (prescriptionDTO.getDoctorId() == null) {
                throw new IllegalArgumentException("ID bác sĩ không được để trống");
            }

            // Get patient and doctor
            PatientProfile patient = patientProfileRepository.findById(prescriptionDTO.getPatientId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bệnh nhân"));

            DocInfo doctor = docInfoRepository.findById(prescriptionDTO.getDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ"));

            // Create prescription
            Prescription prescription = new Prescription();
            prescription.setPrescriptionCode(prescriptionDTO.getPrescriptionCode());
            prescription.setPatientProfile(patient);
            prescription.setDocInfo(doctor);
            prescription.setCreatedAt(new Date());
            prescription.setUpdatedAt(new Date());

            Prescription saved = prescriptionRepository.save(prescription);
            log.info("Prescription created successfully with ID: {} and code: {}", saved.getId(),
                    saved.getPrescriptionCode());

            return saved;

        } catch (IllegalArgumentException e) {
            log.warn("Failed to create prescription: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating prescription", e);
            throw new RuntimeException("Lỗi khi tạo đơn thuốc: " + e.getMessage(), e);
        }
    }

    /**
     * Update prescription (must be within 1 hour of creation)
     * 
     * @param id                    prescription ID
     * @param prescriptionUpdateDTO updated data
     * @return updated Prescription entity
     * @throws IllegalStateException if prescription is older than 1 hour
     */
    @Override
    public Prescription updatePrescription(Long id, PrescriptionUpdateDTO prescriptionUpdateDTO) {
        try {
            // Get current user from SecurityContext (for audit purposes)
            getCurrentUserId();

            // Find prescription to update
            Prescription prescription = prescriptionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn thuốc"));

            // Business Logic: Check if prescription was created within 1 hour
            Date currentTime = new Date();
            long diffInMillis = currentTime.getTime() - prescription.getCreatedAt().getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

            if (diffInHours >= 1) {
                log.warn("Cannot update prescription {} - created {} hours ago", id, diffInHours);
                throw new IllegalStateException(
                        "Không thể chỉnh sửa đơn thuốc. Chỉ được phép chỉnh sửa trong vòng 1 giờ sau khi tạo");
            }

            // Update patient if provided
            if (prescriptionUpdateDTO.getPatientId() != null) {
                PatientProfile patient = patientProfileRepository.findById(prescriptionUpdateDTO.getPatientId())
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bệnh nhân"));
                prescription.setPatientProfile(patient);
            }

            // Update doctor if provided
            if (prescriptionUpdateDTO.getDoctorId() != null) {
                DocInfo doctor = docInfoRepository.findById(prescriptionUpdateDTO.getDoctorId())
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ"));
                prescription.setDocInfo(doctor);
            }

            // Update note if provided
            if (prescriptionUpdateDTO.getNote() != null) {
                prescription.setNote(prescriptionUpdateDTO.getNote());
            }

            prescription.setUpdatedAt(currentTime);

            // Update prescription details if provided
            if (prescriptionUpdateDTO.getPrescriptionDetails() != null
                    && !prescriptionUpdateDTO.getPrescriptionDetails().isEmpty()) {
                // Delete existing prescription details
                List<PrescriptionDetail> existingDetails = prescriptionDetailRepository
                        .findByPrescriptionId(prescription.getId());
                prescriptionDetailRepository.deleteAll(existingDetails);

                // Create new prescription details
                List<PrescriptionDetail> newDetails = new ArrayList<>();
                for (PrescriptionUpdateDTO.PrescriptionDetailDTO detailDTO : prescriptionUpdateDTO
                        .getPrescriptionDetails()) {
                    MedicineInfo medicine = medicineInfoRepository.findById(detailDTO.getMedicineId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Không tìm thấy thuốc với ID: " + detailDTO.getMedicineId()));

                    PrescriptionDetail detail = new PrescriptionDetail();
                    detail.setPrescription(prescription);
                    detail.setMedicineInfo(medicine);
                    detail.setQuantity(detailDTO.getQuantity());
                    detail.setUsageInstructions(detailDTO.getUsageInstructions());
                    newDetails.add(detail);
                }
                prescriptionDetailRepository.saveAll(newDetails);
            }

            Prescription updated = prescriptionRepository.save(prescription);
            log.info("Prescription updated successfully with ID: {}", updated.getId());

            return updated;

        } catch (EntityNotFoundException | IllegalStateException e) {
            log.warn("Failed to update prescription: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating prescription", e);
            throw new RuntimeException("Lỗi khi cập nhật đơn thuốc: " + e.getMessage(), e);
        }
    }

    /**
     * Delete prescription and associated prescription details
     * 
     * @param prescriptionId prescription ID
     */
    @Override
    public void deletePrescription(Long prescriptionId) {
        try {
            // Find prescription to delete
            Prescription prescription = prescriptionRepository.findById(prescriptionId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn thuốc"));

            // Delete all prescription details first (foreign key constraint)
            List<PrescriptionDetail> prescriptionDetails = prescriptionDetailRepository
                    .findByPrescriptionId(prescriptionId);
            if (!prescriptionDetails.isEmpty()) {
                prescriptionDetailRepository.deleteAll(prescriptionDetails);
            }

            // Delete the prescription
            prescriptionRepository.delete(prescription);
            log.info("Prescription deleted successfully with ID: {}", prescriptionId);

        } catch (EntityNotFoundException e) {
            log.warn("Failed to delete prescription: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting prescription", e);
            throw new RuntimeException("Lỗi khi xóa đơn thuốc: " + e.getMessage(), e);
        }
    }

    /**
     * Extract current user ID from SecurityContext
     * 
     * @return current user ID
     * @throws RuntimeException if no user is authenticated
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Không tìm thấy người dùng được xác thực");
        }

        // Extract UserPrincipal from JWT token
        Object principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }

        log.warn("Principal is not UserPrincipal: {}", principal.getClass().getName());
        throw new RuntimeException("Không thể xác định ID người dùng hiện tại");
    }
}
