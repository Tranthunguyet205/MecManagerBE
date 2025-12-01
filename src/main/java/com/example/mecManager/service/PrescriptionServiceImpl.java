package com.example.mecManager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mecManager.model.entity.DocInfo;
import com.example.mecManager.model.entity.MedicineInfo;
import com.example.mecManager.model.entity.Prescription;
import com.example.mecManager.model.entity.PrescriptionDetail;
import com.example.mecManager.dto.PrescriptionCreateDTO;
import com.example.mecManager.dto.PrescriptionUpdateDTO;
import com.example.mecManager.auth.UserPrincipal;
import com.example.mecManager.repository.DocInfoRepository;
import com.example.mecManager.repository.MedicineInfoRepository;
import com.example.mecManager.repository.PrescriptionDetailRepository;
import com.example.mecManager.repository.PrescriptionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DocInfoRepository docInfoRepository;
    private final MedicineInfoRepository medicineInfoRepository;
    private final PrescriptionDetailRepository prescriptionDetailRepository;

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

    @Override
    @Transactional(readOnly = true)
    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Prescription not found with ID: {}", id);
                    return new EntityNotFoundException("Không tìm thấy đơn thuốc với ID: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Object findByDTO(PrescriptionCreateDTO prescriptionDTO) {
        // TODO: Implement search logic if needed
        return null;
    }

    @Override
    public Prescription createPrescription(PrescriptionCreateDTO dto) {
        try {
            getCurrentUserId();

            DocInfo doctor = docInfoRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ"));

            Prescription prescription = Prescription.builder()
                    .prescriptionCode(dto.getPrescriptionCode())
                    .patientName(dto.getPatientName())
                    .patientNationalId(dto.getPatientNationalId())
                    .patientDob(dto.getPatientDob())
                    .patientGender(dto.getPatientGender())
                    .patientAddress(dto.getPatientAddress())
                    .patientPhone(dto.getPatientPhone())
                    .diagnosis(dto.getDiagnosis())
                    .conclusion(dto.getConclusion())
                    .treatmentType(dto.getTreatmentType())
                    .heightCm(dto.getHeightCm())
                    .weightKg(dto.getWeightKg())
                    .note(dto.getNote())
                    .docInfo(doctor)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();

            Prescription saved = prescriptionRepository.save(prescription);
            log.info("Prescription created: ID={}, Code={}, Patient={}", 
                    saved.getId(), saved.getPrescriptionCode(), saved.getPatientName());

            // Create prescription details (medicines)
            if (dto.getMedicines() != null && !dto.getMedicines().isEmpty()) {
                List<PrescriptionDetail> details = new ArrayList<>();
                for (var medicineDTO : dto.getMedicines()) {
                    MedicineInfo medicine = medicineInfoRepository.findById(medicineDTO.getMedicineId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Không tìm thấy thuốc với ID: " + medicineDTO.getMedicineId()));

                    PrescriptionDetail detail = new PrescriptionDetail();
                    detail.setPrescription(saved);
                    detail.setMedicineInfo(medicine);
                    detail.setQuantity(medicineDTO.getQuantity());
                    detail.setUsageInstructions(medicineDTO.getUsageInstructions());
                    details.add(detail);
                }
                prescriptionDetailRepository.saveAll(details);
                saved.setMedicines(details);
                log.info("Created {} prescription details for prescription ID={}", details.size(), saved.getId());
            }

            return saved;

        } catch (IllegalArgumentException e) {
            log.warn("Failed to create prescription: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating prescription", e);
            throw new RuntimeException("Lỗi khi tạo đơn thuốc: " + e.getMessage(), e);
        }
    }

    @Override
    public Prescription updatePrescription(Long id, PrescriptionUpdateDTO dto) {
        try {
            getCurrentUserId();

            Prescription prescription = prescriptionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn thuốc"));

            Date currentTime = new Date();
            long diffInMillis = currentTime.getTime() - prescription.getCreatedAt().getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

            if (diffInHours >= 1) {
                log.warn("Cannot update prescription {} - created {} hours ago", id, diffInHours);
                throw new IllegalStateException(
                        "Không thể chỉnh sửa đơn thuốc. Chỉ được phép chỉnh sửa trong vòng 1 giờ sau khi tạo");
            }

            prescription.setUpdatedAt(currentTime);
            Prescription updated = prescriptionRepository.save(prescription);
            log.info("Prescription updated: ID={}", id);

            return updated;

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating prescription", e);
            throw new RuntimeException("Lỗi khi cập nhật đơn thuốc: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePrescription(Long prescriptionId) {
        try {
            Prescription prescription = prescriptionRepository.findById(prescriptionId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn thuốc"));

            prescriptionRepository.delete(prescription);
            log.info("Prescription deleted: ID={}", prescriptionId);

        } catch (Exception e) {
            log.error("Error deleting prescription", e);
            throw new RuntimeException("Lỗi khi xóa đơn thuốc: " + e.getMessage(), e);
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            throw new RuntimeException("Không thể xác định người dùng hiện tại");
        }
        return ((UserPrincipal) auth.getPrincipal()).getId();
    }
}

