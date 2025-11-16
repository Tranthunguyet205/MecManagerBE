package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.PrescriptionDTO;
import com.example.mecManager.model.PrescriptionResDTO;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.repository.PrescriptionRepository;
import com.example.mecManager.repository.PrescriptionRepositoryJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionRepositoryJdbc prescriptionRepositoryJdbc;

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
}
