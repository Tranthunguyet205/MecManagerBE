package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.DocInfo;
import com.example.mecManager.model.DocInfoDTO;
import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.model.User;
import com.example.mecManager.repository.DocInfoRepository;
import com.example.mecManager.repository.PrescriptionRepository;
import com.example.mecManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DocInfoRepository docInfoRepository;
    private final UserRepository userRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    @Transactional
    public ResponseObject createDoctorInfo(DocInfoDTO docInfoDTO, Long createdBy) {
        try {
            // Validate user exists
            Optional<User> userOptional = userRepository.findById(docInfoDTO.getUserId());
            if (!userOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "User not found", null);
            }

            User user = userOptional.get();

            // Check if doctor info already exists for this user
            DocInfo existingDocInfo = docInfoRepository.findByUserId(docInfoDTO.getUserId());
            if (existingDocInfo != null) {
                return new ResponseObject(AppConstants.STATUS.ALREADY_EXISTS, "Doctor information already exists for this user", null);
            }

            // Validate creator exists
            Optional<User> creatorOptional = userRepository.findById(createdBy);
            if (!creatorOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Creator user not found", null);
            }

            User creator = creatorOptional.get();

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

            return new ResponseObject(AppConstants.STATUS.SUCCESS, "Doctor information created successfully", savedDocInfo);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, "Error creating doctor information: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponseObject getDoctorById(Long doctorId) {
        try {
            Optional<DocInfo> doctorOptional = docInfoRepository.findById(doctorId);
            
            if (!doctorOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, 
                    "Không tìm thấy bác sĩ với ID: " + doctorId, null);
            }

            DocInfo doctor = doctorOptional.get();
            
            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Lấy thông tin bác sĩ thành công", doctor);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi lấy thông tin bác sĩ: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponseObject getAllDoctors(Integer page, Integer pageSize) {
        try {
            // Default values
            int currentPage = (page != null && page > 0) ? page - 1 : 0;
            int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

            // Create pageable with sorting by created date descending
            Pageable pageable = PageRequest.of(currentPage, size, Sort.by("createdAt").descending());
            
            Page<DocInfo> doctorPage = docInfoRepository.findAll(pageable);

            // Prepare response with pagination info
            Map<String, Object> response = new HashMap<>();
            response.put("doctors", doctorPage.getContent());
            response.put("currentPage", doctorPage.getNumber() + 1);
            response.put("totalPages", doctorPage.getTotalPages());
            response.put("totalItems", doctorPage.getTotalElements());
            response.put("pageSize", doctorPage.getSize());

            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Lấy danh sách bác sĩ thành công", response);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi lấy danh sách bác sĩ: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponseObject searchDoctors(String practiceCertificateNo, String licenseNo, Integer page, Integer pageSize) {
        try {
            // If both search criteria provided, search by practiceCertificateNo first
            if (practiceCertificateNo != null && !practiceCertificateNo.trim().isEmpty()) {
                DocInfo doctor = docInfoRepository.findByPracticeCertificateNo(practiceCertificateNo.trim());
                
                if (doctor == null) {
                    return new ResponseObject(AppConstants.STATUS.NOT_FOUND, 
                        "Không tìm thấy bác sĩ với số chứng chỉ hành nghề: " + practiceCertificateNo, null);
                }
                
                return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                    "Tìm thấy bác sĩ theo số chứng chỉ hành nghề", doctor);
            }
            
            // Search by licenseNo
            if (licenseNo != null && !licenseNo.trim().isEmpty()) {
                DocInfo doctor = docInfoRepository.findByLicenseNo(licenseNo.trim());
                
                if (doctor == null) {
                    return new ResponseObject(AppConstants.STATUS.NOT_FOUND, 
                        "Không tìm thấy bác sĩ với số giấy phép hành nghề: " + licenseNo, null);
                }
                
                return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                    "Tìm thấy bác sĩ theo số giấy phép hành nghề", doctor);
            }
            
            // If no search criteria provided, return all doctors with pagination
            return getAllDoctors(page, pageSize);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi tìm kiếm bác sĩ: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseObject deleteDoctor(Long doctorId, Long deletedBy) {
        try {
            // Validate doctor exists
            Optional<DocInfo> doctorOptional = docInfoRepository.findById(doctorId);
            if (!doctorOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, 
                    "Không tìm thấy bác sĩ với ID: " + doctorId, null);
            }

            // Validate deleter exists
            Optional<User> deleterOptional = userRepository.findById(deletedBy);
            if (!deleterOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy người xóa", null);
            }

            DocInfo doctor = doctorOptional.get();

            // Check if doctor has any prescriptions
            List<Prescription> prescriptions = prescriptionRepository.findByDoctorId(doctorId);

            if (!prescriptions.isEmpty()) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, 
                    "Không thể xóa bác sĩ. Bác sĩ có " + prescriptions.size() + " đơn thuốc đã được kê", 
                    null);
            }

            // Delete the doctor
            docInfoRepository.delete(doctor);

            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Xóa thông tin bác sĩ thành công", null);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi xóa bác sĩ: " + e.getMessage(), null);
        }
    }
}
