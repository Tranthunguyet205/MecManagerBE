package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.MedicineInfo;
import com.example.mecManager.model.MedicineInfoDTO;
import com.example.mecManager.model.PrescriptionDetail;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.model.User;
import com.example.mecManager.repository.MedicineInfoRepository;
import com.example.mecManager.repository.PrescriptionDetailRepository;
import com.example.mecManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineInfoRepository medicineInfoRepository;
    private final UserRepository userRepository;
    private final PrescriptionDetailRepository prescriptionDetailRepository;

    @Override
    @Transactional
    public ResponseObject createMedicine(MedicineInfoDTO medicineInfoDTO, Long createdBy) {
        try {
            // Validate creator exists
            Optional<User> creatorOptional = userRepository.findById(createdBy);
            if (!creatorOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Creator user not found", null);
            }

            User creator = creatorOptional.get();

            // Validate required fields
            if (medicineInfoDTO.getMedicineCode() == null || medicineInfoDTO.getMedicineCode().trim().isEmpty()) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, "Medicine code is required", null);
            }
            if (medicineInfoDTO.getMedicineName() == null || medicineInfoDTO.getMedicineName().trim().isEmpty()) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, "Medicine name is required", null);
            }
            if (medicineInfoDTO.getIngredient() == null || medicineInfoDTO.getIngredient().trim().isEmpty()) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, "Ingredient is required", null);
            }
            if (medicineInfoDTO.getQuantity() == null || medicineInfoDTO.getQuantity() < 0) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, "Valid quantity is required", null);
            }
            if (medicineInfoDTO.getUsageInstructions() == null || medicineInfoDTO.getUsageInstructions().trim().isEmpty()) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, "Usage instructions are required", null);
            }

            // Create new MedicineInfo
            MedicineInfo medicineInfo = new MedicineInfo();
            medicineInfo.setMedicineCode(medicineInfoDTO.getMedicineCode());
            medicineInfo.setIngredient(medicineInfoDTO.getIngredient());
            medicineInfo.setMedicineName(medicineInfoDTO.getMedicineName());
            medicineInfo.setQuantity(medicineInfoDTO.getQuantity());
            medicineInfo.setUsageInstructions(medicineInfoDTO.getUsageInstructions());
            medicineInfo.setCreatedAt(new Date());
            medicineInfo.setUpdatedAt(new Date());
            medicineInfo.setUserCreateBy(creator);

            MedicineInfo savedMedicine = medicineInfoRepository.save(medicineInfo);

            return new ResponseObject(AppConstants.STATUS.SUCCESS, "Medicine created successfully", savedMedicine);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, "Error creating medicine: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseObject deleteMedicine(Long medicineId, Long deletedBy) {
        try {
            // Validate medicine exists
            Optional<MedicineInfo> medicineOptional = medicineInfoRepository.findById(medicineId);
            if (!medicineOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, 
                    "Không tìm thấy thuốc với ID: " + medicineId, null);
            }

            // Validate deleter exists
            Optional<User> deleterOptional = userRepository.findById(deletedBy);
            if (!deleterOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy người xóa", null);
            }

            MedicineInfo medicine = medicineOptional.get();

            // Check if medicine is being used in any prescription
            List<PrescriptionDetail> prescriptionDetails = prescriptionDetailRepository.findByMedicineId(medicineId);

            if (!prescriptionDetails.isEmpty()) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, 
                    "Không thể xóa thuốc. Thuốc đang được sử dụng trong " + prescriptionDetails.size() + " đơn thuốc", 
                    null);
            }

            // Delete the medicine
            medicineInfoRepository.delete(medicine);

            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Xóa thuốc thành công", null);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi xóa thuốc: " + e.getMessage(), null);
        }
    }
}
