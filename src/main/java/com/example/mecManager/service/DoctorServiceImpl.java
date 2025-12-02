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

import com.example.mecManager.model.entity.DocInfo;
import com.example.mecManager.dto.DocInfoDTO;
import com.example.mecManager.dto.DocInfoUpdateDTO;
import com.example.mecManager.model.entity.User;
import com.example.mecManager.auth.UserPrincipal;
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
    private final FileService fileService;

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
            docInfo.setWorkplace(docInfoDTO.getWorkplace());
            docInfo.setPracticeCertificateUrl(docInfoDTO.getPracticeCertificateUrl());
            docInfo.setLicenseUrl(docInfoDTO.getLicenseUrl());
            docInfo.setNationalIdUrl(docInfoDTO.getNationalIdUrl());
            docInfo.setCreatedAt(new Date());
            docInfo.setCreatedBy(creator);

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
    public Object getDoctors(
            String search, String practiceCertificateNo, String licenseNo, Integer page, Integer pageSize) {
        
        // Search by name or email (single search field)
        if (search != null && !search.trim().isEmpty()) {
            List<DocInfo> doctors = docInfoRepository.findBySearchTerm(search.trim());
            if (doctors.isEmpty()) {
                throw new RuntimeException("Không tìm thấy bác sĩ với tên hoặc email: " + search);
            }
            return buildSearchResultResponse(doctors);
        }

        // Search by practice certificate number
        if (practiceCertificateNo != null && !practiceCertificateNo.trim().isEmpty()) {
            List<DocInfo> doctors = docInfoRepository.findByPracticeCertificateNo(practiceCertificateNo.trim());
            if (doctors.isEmpty()) {
                throw new RuntimeException("Không tìm thấy bác sĩ với chứng chỉ: " + practiceCertificateNo);
            }
            return buildSearchResultResponse(doctors);
        }

        // Search by license number
        if (licenseNo != null && !licenseNo.trim().isEmpty()) {
            List<DocInfo> doctors = docInfoRepository.findByLicenseNo(licenseNo.trim());
            if (doctors.isEmpty()) {
                throw new RuntimeException("Không tìm thấy bác sĩ với giấy phép: " + licenseNo);
            }
            return buildSearchResultResponse(doctors);
        }

        // If no search criteria, return all doctors
        return getAllDoctors(page, pageSize);
    }

    private Map<String, Object> buildSearchResultResponse(List<DocInfo> doctors) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", doctors.stream().map(this::mapToDTO).toList());
        response.put("page", 0);
        response.put("size", doctors.size());
        response.put("totalElements", (long) doctors.size());
        response.put("totalPages", 1);
        response.put("isFirst", true);
        response.put("isLast", true);
        return response;
    }

    @Override
    public DocInfoDTO updateDoctor(Long id, DocInfoUpdateDTO docInfoUpdateDTO) {
        try {
            Long updatedBy = getCurrentUserId();
            User updater = userRepository
                    .findById(updatedBy)
                    .orElseThrow(() -> new RuntimeException("Người cập nhật không tồn tại"));

            // First try to find as DocInfo ID
            DocInfo doctor = docInfoRepository.findById(id).orElse(null);
            
            // If not found as DocInfo ID, try to find as User ID and get their DocInfo
            if (doctor == null) {
                User user = userRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ hoặc người dùng với ID: " + id));
                
                doctor = docInfoRepository.findByUserId(id);
                
                // If DocInfo doesn't exist for this user, create it
                if (doctor == null) {
                    doctor = DocInfo.builder()
                            .user(user)
                            .fullName(docInfoUpdateDTO.getFullName())
                            .dob(docInfoUpdateDTO.getDob())
                            .phone(docInfoUpdateDTO.getPhone())
                            .cccd(docInfoUpdateDTO.getCccd())
                            .cccdIssueDate(docInfoUpdateDTO.getCccdIssueDate())
                            .cccdIssuePlace(docInfoUpdateDTO.getCccdIssuePlace())
                            .currentAddress(docInfoUpdateDTO.getCurrentAddress())
                            .email(docInfoUpdateDTO.getEmail())
                            .practiceCertificateNo(docInfoUpdateDTO.getPracticeCertificateNo())
                            .practiceCertificateIssueDate(docInfoUpdateDTO.getPracticeCertificateIssueDate())
                            .practiceCertificateIssuePlace(docInfoUpdateDTO.getPracticeCertificateIssuePlace())
                            .licenseNo(docInfoUpdateDTO.getLicenseNo())
                            .licenseIssueDate(docInfoUpdateDTO.getLicenseIssueDate())
                            .licenseIssuePlace(docInfoUpdateDTO.getLicenseIssuePlace())
                            .workplace(docInfoUpdateDTO.getWorkplace())
                            .practiceCertificateUrl(docInfoUpdateDTO.getPracticeCertificateUrl())
                            .licenseUrl(docInfoUpdateDTO.getLicenseUrl())
                            .nationalIdUrl(docInfoUpdateDTO.getNationalIdUrl())
                            .createdAt(new Date())
                            .updatedAt(new Date())
                            .createdBy(updater)
                            .updatedBy(updater)
                            .build();
                    
                    doctor = docInfoRepository.save(doctor);
                    return mapToDTO(doctor);
                }
            }

            // Update existing DocInfo
            doctor.setFullName(docInfoUpdateDTO.getFullName());
            doctor.setDob(docInfoUpdateDTO.getDob());
            doctor.setPhone(docInfoUpdateDTO.getPhone());
            doctor.setCccd(docInfoUpdateDTO.getCccd());
            doctor.setCccdIssueDate(docInfoUpdateDTO.getCccdIssueDate());
            doctor.setCccdIssuePlace(docInfoUpdateDTO.getCccdIssuePlace());
            doctor.setCurrentAddress(docInfoUpdateDTO.getCurrentAddress());
            doctor.setEmail(docInfoUpdateDTO.getEmail());
            doctor.setPracticeCertificateNo(docInfoUpdateDTO.getPracticeCertificateNo());
            doctor.setPracticeCertificateIssueDate(docInfoUpdateDTO.getPracticeCertificateIssueDate());
            doctor.setPracticeCertificateIssuePlace(docInfoUpdateDTO.getPracticeCertificateIssuePlace());
            doctor.setLicenseNo(docInfoUpdateDTO.getLicenseNo());
            doctor.setLicenseIssueDate(docInfoUpdateDTO.getLicenseIssueDate());
            doctor.setLicenseIssuePlace(docInfoUpdateDTO.getLicenseIssuePlace());
            doctor.setWorkplace(docInfoUpdateDTO.getWorkplace());

            // Update file URLs if provided
            if (docInfoUpdateDTO.getPracticeCertificateUrl() != null) {
                doctor.setPracticeCertificateUrl(docInfoUpdateDTO.getPracticeCertificateUrl());
            }
            if (docInfoUpdateDTO.getLicenseUrl() != null) {
                doctor.setLicenseUrl(docInfoUpdateDTO.getLicenseUrl());
            }
            if (docInfoUpdateDTO.getNationalIdUrl() != null) {
                doctor.setNationalIdUrl(docInfoUpdateDTO.getNationalIdUrl());
            }

            doctor.setUpdatedAt(new Date());
            doctor.setUpdatedBy(updater);

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

            // Delete files from MinIO
            fileService.deleteAllFiles("Doctor", id);

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
                .workplace(docInfo.getWorkplace())
                .practiceCertificateUrl(docInfo.getPracticeCertificateUrl())
                .licenseUrl(docInfo.getLicenseUrl())
                .nationalIdUrl(docInfo.getNationalIdUrl())
                .createdAt(docInfo.getCreatedAt())
                .updatedAt(docInfo.getUpdatedAt())
                .username(docInfo.getUser().getUsername())
                .status(docInfo.getUser().getStatus().name())
                .role(docInfo.getUser().getRole().name())
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
