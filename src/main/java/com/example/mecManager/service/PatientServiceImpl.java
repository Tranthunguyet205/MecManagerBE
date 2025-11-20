package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.PatientProfile;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.repository.PatientProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientProfileRepository patientProfileRepository;

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
}
