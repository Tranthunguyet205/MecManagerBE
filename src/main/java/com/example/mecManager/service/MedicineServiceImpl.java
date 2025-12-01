package com.example.mecManager.service;

import com.example.mecManager.model.entity.MedicineInfo;
import com.example.mecManager.dto.MedicineInfoDTO;
import com.example.mecManager.model.entity.PrescriptionDetail;
import com.example.mecManager.model.entity.User;
import com.example.mecManager.auth.UserPrincipal;
import com.example.mecManager.repository.MedicineInfoRepository;
import com.example.mecManager.repository.PrescriptionDetailRepository;
import com.example.mecManager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for Medicine management
 * Handles medicine CRUD operations and pharmacy data
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MedicineServiceImpl implements MedicineService {

    private final MedicineInfoRepository medicineInfoRepository;
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final UserRepository userRepository;

    /**
     * Get medicine by ID
     * 
     * @param id medicine ID
     * @return MedicineInfo entity
     * @throws EntityNotFoundException if not found
     */
    @Override
    @Transactional(readOnly = true)
    public MedicineInfo getMedicineById(Long id) {
        return medicineInfoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Medicine not found with ID: {}", id);
                    return new EntityNotFoundException("Không tìm thấy thuốc với ID: " + id);
                });
    }

    /**
     * Get all medicines with pagination
     * 
     * @param page     zero-based page number (default 0)
     * @param pageSize number of records per page (default 10)
     * @return Map containing paginated medicines list and metadata
     */
    @Override
    @Transactional(readOnly = true)
    public Object getAllMedicines(Integer page, Integer pageSize) {
        // Default values: page 0, size 10
        int currentPage = (page != null && page >= 0) ? page : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        try {
            // Create pageable with sorting by created date descending
            Pageable pageable = PageRequest.of(currentPage, size, Sort.by("createdAt").descending());
            Page<MedicineInfo> medicinePage = medicineInfoRepository.findAll(pageable);

            // Build response with pagination metadata (Spring Data Convention)
            Map<String, Object> response = new HashMap<>();
            response.put("content", medicinePage.getContent());
            response.put("page", medicinePage.getNumber());
            response.put("size", medicinePage.getSize());
            response.put("totalElements", medicinePage.getTotalElements());
            response.put("totalPages", medicinePage.getTotalPages());
            response.put("isFirst", medicinePage.isFirst());
            response.put("isLast", medicinePage.isLast());

            return response;

        } catch (Exception e) {
            log.error("Error retrieving medicines list", e);
            throw new RuntimeException("Lỗi khi lấy danh sách thuốc: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new medicine
     * 
     * @param medicineDTO medicine data
     * @return created MedicineInfo entity
     */
    @Override
    public MedicineInfo createMedicine(MedicineInfoDTO medicineDTO) {
        try {
            // Get current user from SecurityContext (for audit purposes)
            Long userId = getCurrentUserId();

            // Validate required fields
            validateMedicineDTO(medicineDTO);

            // Create new MedicineInfo
            MedicineInfo medicineInfo = new MedicineInfo();
            medicineInfo.setMedicineCode(medicineDTO.getMedicineCode());
            medicineInfo.setMedicineName(medicineDTO.getMedicineName());
            medicineInfo.setIngredient(medicineDTO.getIngredient());
            medicineInfo.setQuantity(medicineDTO.getQuantity());
            medicineInfo.setUsageInstructions(medicineDTO.getUsageInstructions());
            medicineInfo.setCreatedAt(new Date());
            medicineInfo.setUpdatedAt(new Date());

            // Set audit fields
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            medicineInfo.setUserCreateBy(currentUser);

            MedicineInfo saved = medicineInfoRepository.save(medicineInfo);
            log.info("Medicine created successfully with ID: {} and code: {}", saved.getId(), saved.getMedicineCode());

            return saved;

        } catch (IllegalArgumentException e) {
            log.warn("Failed to create medicine: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating medicine", e);
            throw new RuntimeException("Lỗi khi tạo thuốc: " + e.getMessage(), e);
        }
    }

    /**
     * Update medicine information
     * 
     * @param id          medicine ID
     * @param medicineDTO updated medicine data
     * @return updated MedicineInfo entity
     * @throws EntityNotFoundException if medicine not found
     */
    @Override
    public MedicineInfo updateMedicine(Long id, MedicineInfoDTO medicineDTO) {
        try {
            // Get current user from SecurityContext (for audit purposes)
            getCurrentUserId();

            // Find medicine to update
            MedicineInfo medicine = medicineInfoRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Medicine not found with ID: {}", id);
                        return new EntityNotFoundException("Không tìm thấy thuốc với ID: " + id);
                    });

            // Validate DTO fields
            validateMedicineDTO(medicineDTO);

            // Update fields
            medicine.setMedicineCode(medicineDTO.getMedicineCode());
            medicine.setMedicineName(medicineDTO.getMedicineName());
            medicine.setIngredient(medicineDTO.getIngredient());
            medicine.setQuantity(medicineDTO.getQuantity());
            medicine.setUsageInstructions(medicineDTO.getUsageInstructions());
            medicine.setUpdatedAt(new Date());

            MedicineInfo updated = medicineInfoRepository.save(medicine);
            log.info("Medicine updated successfully with ID: {}", updated.getId());

            return updated;

        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warn("Failed to update medicine: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating medicine", e);
            throw new RuntimeException("Lỗi khi cập nhật thuốc: " + e.getMessage(), e);
        }
    }

    /**
     * Delete medicine by ID
     * Note: Medicines with associated prescription details cannot be deleted
     * 
     * @param id medicine ID
     */
    @Override
    public void deleteMedicine(Long id) {
        try {
            // Find medicine to delete
            MedicineInfo medicine = medicineInfoRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Medicine not found with ID: {}", id);
                        return new EntityNotFoundException("Không tìm thấy thuốc với ID: " + id);
                    });

            // Check if medicine is being used in any prescription
            List<PrescriptionDetail> prescriptionDetails = prescriptionDetailRepository.findByMedicineId(id);

            if (!prescriptionDetails.isEmpty()) {
                log.warn("Cannot delete medicine {} - used in {} prescriptions", id, prescriptionDetails.size());
                throw new IllegalStateException(
                        "Không thể xóa thuốc. Thuốc đang được sử dụng trong " + prescriptionDetails.size()
                                + " đơn thuốc");
            }

            // Delete the medicine
            medicineInfoRepository.delete(medicine);
            log.info("Medicine deleted successfully with ID: {}", id);

        } catch (EntityNotFoundException | IllegalStateException e) {
            log.warn("Failed to delete medicine: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting medicine", e);
            throw new RuntimeException("Lỗi khi xóa thuốc: " + e.getMessage(), e);
        }
    }

    /**
     * Validate MedicineDTO required fields
     * 
     * @param medicineDTO medicine data to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateMedicineDTO(MedicineInfoDTO medicineDTO) {
        if (medicineDTO.getMedicineCode() == null || medicineDTO.getMedicineCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã thuốc không được để trống");
        }
        if (medicineDTO.getMedicineName() == null || medicineDTO.getMedicineName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thuốc không được để trống");
        }
        if (medicineDTO.getIngredient() == null || medicineDTO.getIngredient().trim().isEmpty()) {
            throw new IllegalArgumentException("Thành phần không được để trống");
        }
        if (medicineDTO.getQuantity() == null || medicineDTO.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng phải là số dương");
        }
        if (medicineDTO.getUsageInstructions() == null || medicineDTO.getUsageInstructions().trim().isEmpty()) {
            throw new IllegalArgumentException("Hướng dẫn sử dụng không được để trống");
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
