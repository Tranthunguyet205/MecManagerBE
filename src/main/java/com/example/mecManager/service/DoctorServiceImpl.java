package com.example.mecManager.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mecManager.model.DocInfo;
import com.example.mecManager.model.DocInfoDTO;
import com.example.mecManager.model.User;
import com.example.mecManager.model.UserPrincipal;
import com.example.mecManager.repository.DocInfoRepository;
import com.example.mecManager.repository.PrescriptionRepository;
import com.example.mecManager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DocInfoRepository docInfoRepository;
    private final UserRepository userRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    public DocInfoDTO createDoctor(DocInfoDTO docInfoDTO) {
        try {
            // Get current authenticated user
            Long createdBy = getCurrentUserId();

            // Validate user exists
            User user = userRepository
                    .findById(docInfoDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

            // Check if doctor info already exists for this user
            if (docInfoRepository.findByUserId(docInfoDTO.getUserId()) != null) {
                throw new RuntimeException("Thông tin bác sĩ đã tồn tại cho người dùng này");
            }

            // Get creator
            User creator = userRepository
                    .findById(createdBy)
                    .orElseThrow(() -> new RuntimeException("Người tạo không tồn tại"));

            // Create new DocInfo
            DocInfo docInfo = new DocInfo();
            docInfo.setUser(user);
            docInfo.setFullName(docInfoDTO.getFullName());
            docInfo.setDob(docInfoDTO.getDob());
            docInfo.setPhone(docInfoDTO.getPhone());
            docInfo.setCccd(docInfoDTO.getCccd());
            docInfo.setCccdIssueDate(docInfoDTO.getCccdIssueDate());
            docInfo.setCccdIssuePlace(docInfoDTO.getCccdIssuePlace());
            docInfo.setCurrentAddress(docInfoDTO.getCurrentAddress());
            docInfo.setEmail(docInfoDTO.getEmail());
            docInfo.setPracticeCertificateNo(docInfoDTO.getPracticeCertificateNo());
            docInfo.setPracticeCertificateIssueDate(docInfoDTO.getPracticeCertificateIssueDate());
            docInfo.setPracticeCertificateIssuePlace(docInfoDTO.getPracticeCertificateIssuePlace());
            docInfo.setLicenseNo(docInfoDTO.getLicenseNo());
            docInfo.setLicenseIssueDate(docInfoDTO.getLicenseIssueDate());
            docInfo.setLicenseIssuePlace(docInfoDTO.getLicenseIssuePlace());
            docInfo.setCreatedAt(new Date());
            docInfo.setUserCreateBy(creator);

            DocInfo savedDocInfo = docInfoRepository.save(docInfo);
            return mapToDTO(savedDocInfo);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo thông tin bác sĩ: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DocInfoDTO getDoctorById(Long id) {
        DocInfo doctor = docInfoRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + id));
        return mapToDTO(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getAllDoctors(Integer page, Integer pageSize) {
        int pageNum = page != null && page >= 0 ? page : 0;
        int size = pageSize != null && pageSize > 0 ? pageSize : 10;

        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("createdAt").descending());
        Page<DocInfo> doctorPage = docInfoRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", doctorPage.getContent().stream().map(this::mapToDTO).toList());
        response.put("page", doctorPage.getNumber());
        response.put("size", doctorPage.getSize());
        response.put("totalElements", doctorPage.getTotalElements());
        response.put("totalPages", doctorPage.getTotalPages());
        response.put("isFirst", doctorPage.isFirst());
        response.put("isLast", doctorPage.isLast());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object searchDoctors(
            String practiceCertificateNo, String licenseNo, Integer page, Integer pageSize) {
        // Search by practice certificate number
        if (practiceCertificateNo != null && !practiceCertificateNo.trim().isEmpty()) {
            DocInfo doctor = docInfoRepository.findByPracticeCertificateNo(practiceCertificateNo.trim());
            if (doctor == null) {
                throw new RuntimeException("Không tìm thấy bác sĩ với chứng chỉ: " + practiceCertificateNo);
            }
            return mapToDTO(doctor);
        }

        // Search by license number
        if (licenseNo != null && !licenseNo.trim().isEmpty()) {
            DocInfo doctor = docInfoRepository.findByLicenseNo(licenseNo.trim());
            if (doctor == null) {
                throw new RuntimeException("Không tìm thấy bác sĩ với giấy phép: " + licenseNo);
            }
            return mapToDTO(doctor);
        }

        // If no search criteria, return all doctors
        return getAllDoctors(page, pageSize);
    }

    @Override
    public DocInfoDTO updateDoctor(Long id, DocInfoDTO docInfoDTO) {
        try {
            DocInfo doctor = docInfoRepository
                    .findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + id));

            Long updatedBy = getCurrentUserId();
            User updater = userRepository
                    .findById(updatedBy)
                    .orElseThrow(() -> new RuntimeException("Người cập nhật không tồn tại"));

            // Update fields
            doctor.setFullName(docInfoDTO.getFullName());
            doctor.setDob(docInfoDTO.getDob());
            doctor.setPhone(docInfoDTO.getPhone());
            doctor.setCccd(docInfoDTO.getCccd());
            doctor.setCccdIssueDate(docInfoDTO.getCccdIssueDate());
            doctor.setCccdIssuePlace(docInfoDTO.getCccdIssuePlace());
            doctor.setCurrentAddress(docInfoDTO.getCurrentAddress());
            doctor.setEmail(docInfoDTO.getEmail());
            doctor.setPracticeCertificateNo(docInfoDTO.getPracticeCertificateNo());
            doctor.setPracticeCertificateIssueDate(docInfoDTO.getPracticeCertificateIssueDate());
            doctor.setPracticeCertificateIssuePlace(docInfoDTO.getPracticeCertificateIssuePlace());
            doctor.setLicenseNo(docInfoDTO.getLicenseNo());
            doctor.setLicenseIssueDate(docInfoDTO.getLicenseIssueDate());
            doctor.setLicenseIssuePlace(docInfoDTO.getLicenseIssuePlace());
            doctor.setUpdatedAt(new Date());
            doctor.setUserUpdateBy(updater);

            DocInfo updatedDoctor = docInfoRepository.save(doctor);
            return mapToDTO(updatedDoctor);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật bác sĩ: " + e.getMessage());
        }
    }

    @Override
    public void deleteDoctor(Long id) {
        try {
            DocInfo doctor = docInfoRepository
                    .findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + id));

            // Check if doctor has active prescriptions
            List<?> prescriptions = prescriptionRepository.findByDoctorId(id);
            if (!prescriptions.isEmpty()) {
                throw new RuntimeException(
                        "Không thể xóa bác sĩ. Bác sĩ có " + prescriptions.size() + " đơn thuốc");
            }

            docInfoRepository.delete(doctor);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xóa bác sĩ: " + e.getMessage());
        }
    }

    /** Map DocInfo entity to DocInfoDTO */
    private DocInfoDTO mapToDTO(DocInfo docInfo) {
        return DocInfoDTO.builder()
                .id(docInfo.getId())
                .userId(docInfo.getUser().getId())
                .fullName(docInfo.getFullName())
                .dob(docInfo.getDob())
                .phone(docInfo.getPhone())
                .cccd(docInfo.getCccd())
                .cccdIssueDate(docInfo.getCccdIssueDate())
                .cccdIssuePlace(docInfo.getCccdIssuePlace())
                .currentAddress(docInfo.getCurrentAddress())
                .email(docInfo.getEmail())
                .practiceCertificateNo(docInfo.getPracticeCertificateNo())
                .practiceCertificateIssueDate(docInfo.getPracticeCertificateIssueDate())
                .practiceCertificateIssuePlace(docInfo.getPracticeCertificateIssuePlace())
                .licenseNo(docInfo.getLicenseNo())
                .licenseIssueDate(docInfo.getLicenseIssueDate())
                .licenseIssuePlace(docInfo.getLicenseIssuePlace())
                .createdAt(docInfo.getCreatedAt())
                .updatedAt(docInfo.getUpdatedAt())
                .build();
    }

    /** Get current authenticated user ID from SecurityContext */
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

        throw new RuntimeException("Không thể xác định ID người dùng hiện tại");
    }
}
