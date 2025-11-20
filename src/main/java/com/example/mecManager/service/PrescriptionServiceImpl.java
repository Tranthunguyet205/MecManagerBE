package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.*;
import com.example.mecManager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionRepositoryJdbc prescriptionRepositoryJdbc;
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DocInfoRepository docInfoRepository;
    private final MedicineInfoRepository medicineInfoRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseObject getPrescriptionByCode(String code) {
        Prescription prescription = prescriptionRepository.findPrescriptionByPrescriptionCode(code);
        if(prescription != null){
            return new ResponseObject(AppConstants.STATUS.SUCCESS,"", prescription);
        }else {
            return new ResponseObject(AppConstants.STATUS.NOT_FOUND,"", null);
        }
    }

    @Override
    public ResponseObject getPrescriptionById(Long id) {
        Optional<Prescription> prescription = prescriptionRepository.findById(id);
        if(prescription.isPresent()) {
            return new ResponseObject(AppConstants.STATUS.SUCCESS,"", prescription.get());
        }else {
            return new ResponseObject(AppConstants.STATUS.NOT_FOUND,"", null);
        }
    }

    @Override
    public ResponseObject findByDTO(PrescriptionDTO prescriptionDTO) {
        try{
            PrescriptionResDTO prescriptionDTOList = prescriptionRepositoryJdbc.findPrescriptionsByDTO(prescriptionDTO);
            return new ResponseObject(AppConstants.STATUS.SUCCESS,"Tìm kiếm đơn thuốc thành công", prescriptionDTOList);
        }catch (Exception e){
            return new ResponseObject(AppConstants.STATUS.BAD_REQUEST,"Có lỗi dữ liệu truyền vào", null);
        }
    }

    @Override
    @Transactional
    public ResponseObject updatePrescription(PrescriptionUpdateDTO prescriptionUpdateDTO, Long updatedBy) {
        try {
            // Validate prescription exists
            Optional<Prescription> prescriptionOptional = prescriptionRepository.findById(prescriptionUpdateDTO.getPrescriptionId());
            if (!prescriptionOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy đơn thuốc", null);
            }

            Prescription prescription = prescriptionOptional.get();

            // Check if prescription was created within 1 hour
            Date currentTime = new Date();
            long diffInMillis = currentTime.getTime() - prescription.getCreatedAt().getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

            if (diffInHours >= 1) {
                return new ResponseObject(AppConstants.STATUS.BAD_REQUEST, 
                    "Không thể chỉnh sửa đơn thuốc. Chỉ được phép chỉnh sửa trong vòng 1 giờ sau khi tạo", null);
            }

            // Validate updater exists
            Optional<User> updaterOptional = userRepository.findById(updatedBy);
            if (!updaterOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy người cập nhật", null);
            }

            // Update patient if provided
            if (prescriptionUpdateDTO.getPatientId() != null) {
                Optional<PatientProfile> patientOptional = patientProfileRepository.findById(prescriptionUpdateDTO.getPatientId());
                if (!patientOptional.isPresent()) {
                    return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy bệnh nhân", null);
                }
                prescription.setPatientProfile(patientOptional.get());
            }

            // Update doctor if provided
            if (prescriptionUpdateDTO.getDoctorId() != null) {
                Optional<DocInfo> doctorOptional = docInfoRepository.findById(prescriptionUpdateDTO.getDoctorId());
                if (!doctorOptional.isPresent()) {
                    return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy bác sĩ", null);
                }
                prescription.setDocInfo(doctorOptional.get());
            }

            // Update note
            if (prescriptionUpdateDTO.getNote() != null) {
                prescription.setNote(prescriptionUpdateDTO.getNote());
            }

            prescription.setUpdatedAt(currentTime);

            // Update prescription details if provided
            if (prescriptionUpdateDTO.getPrescriptionDetails() != null && !prescriptionUpdateDTO.getPrescriptionDetails().isEmpty()) {
                // Delete existing prescription details
                List<PrescriptionDetail> existingDetails = prescriptionDetailRepository.findByPrescriptionId(prescription.getId());
                prescriptionDetailRepository.deleteAll(existingDetails);

                // Create new prescription details
                List<PrescriptionDetail> newDetails = new ArrayList<>();
                for (PrescriptionUpdateDTO.PrescriptionDetailDTO detailDTO : prescriptionUpdateDTO.getPrescriptionDetails()) {
                    Optional<MedicineInfo> medicineOptional = medicineInfoRepository.findById(detailDTO.getMedicineId());
                    if (!medicineOptional.isPresent()) {
                        return new ResponseObject(AppConstants.STATUS.NOT_FOUND, 
                            "Không tìm thấy thuốc với ID: " + detailDTO.getMedicineId(), null);
                    }

                    PrescriptionDetail detail = new PrescriptionDetail();
                    detail.setPrescription(prescription);
                    detail.setMedicineInfo(medicineOptional.get());
                    detail.setQuantity(detailDTO.getQuantity());
                    detail.setUsageInstructions(detailDTO.getUsageInstructions());
                    newDetails.add(detail);
                }
                prescriptionDetailRepository.saveAll(newDetails);
            }

            Prescription updatedPrescription = prescriptionRepository.save(prescription);

            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Cập nhật đơn thuốc thành công", updatedPrescription);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi cập nhật đơn thuốc: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseObject deletePrescription(Long prescriptionId, Long deletedBy) {
        try {
            // Validate prescription exists
            Optional<Prescription> prescriptionOptional = prescriptionRepository.findById(prescriptionId);
            if (!prescriptionOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy đơn thuốc", null);
            }

            Prescription prescription = prescriptionOptional.get();

            // Validate deleter exists
            Optional<User> deleterOptional = userRepository.findById(deletedBy);
            if (!deleterOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Không tìm thấy người xóa", null);
            }

            // Delete all prescription details first (foreign key constraint)
            List<PrescriptionDetail> prescriptionDetails = prescriptionDetailRepository.findByPrescriptionId(prescriptionId);
            if (!prescriptionDetails.isEmpty()) {
                prescriptionDetailRepository.deleteAll(prescriptionDetails);
            }

            // Delete the prescription
            prescriptionRepository.delete(prescription);

            return new ResponseObject(AppConstants.STATUS.SUCCESS, 
                "Xóa đơn thuốc thành công", null);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, 
                "Lỗi khi xóa đơn thuốc: " + e.getMessage(), null);
        }
    }
}
