package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.PatientProfile;
import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.model.User;
import com.example.mecManager.repository.PatientProfileRepository;
import com.example.mecManager.repository.PrescriptionRepository;
import com.example.mecManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientProfileRepository patientProfileRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseObject getPatientById(Long patientId) {
        try {
            Optional<PatientProfile> patientOptional = patientProfileRepository.findById(patientId);
            
            if (!patientOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, 
                    "Không tìm thấy bệnh nhân với ID: " + patientId, null);
            }

            PatientProfile patient = patientOptional.get();
            
            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Lấy thông tin bệnh nhân thành công", patient);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi lấy thông tin bệnh nhân: " + e.getMessage(), null);
        }
    }

    @Override
    public ResponseObject getAllPatients(Integer page, Integer pageSize) {
        try {
            // Default values
            int currentPage = (page != null && page > 0) ? page - 1 : 0;
            int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

            // Create pageable with sorting by created date descending
            Pageable pageable = PageRequest.of(currentPage, size, Sort.by("createdAt").descending());
            
            Page<PatientProfile> patientPage = patientProfileRepository.findAll(pageable);

            // Prepare response with pagination info
            Map<String, Object> response = new HashMap<>();
            response.put("patients", patientPage.getContent());
            response.put("currentPage", patientPage.getNumber() + 1);
            response.put("totalPages", patientPage.getTotalPages());
            response.put("totalItems", patientPage.getTotalElements());
            response.put("pageSize", patientPage.getSize());

            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Lấy danh sách bệnh nhân thành công", response);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi lấy danh sách bệnh nhân: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseObject deletePatient(Long patientId, Long deletedBy) {
        try {
            // Validate patient exists
            Optional<PatientProfile> patientOptional = patientProfileRepository.findById(patientId);
            if (!patientOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, 
                    "Không tìm thấy bệnh nhân với ID: " + patientId, null);
            }

            // Validate deleter exists
            Optional<User> deleterOptional = userRepository.findById(deletedBy);
            if (!deleterOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy người xóa", null);
            }

            PatientProfile patient = patientOptional.get();

            // Check if patient has any prescriptions
            List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);

            if (!prescriptions.isEmpty()) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, 
                    "Không thể xóa bệnh nhân. Bệnh nhân có " + prescriptions.size() + " đơn thuốc đã được kê", 
                    null);
            }

            // Delete the patient
            patientProfileRepository.delete(patient);

            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Xóa thông tin bệnh nhân thành công", null);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi xóa bệnh nhân: " + e.getMessage(), null);
        }
    }
}
