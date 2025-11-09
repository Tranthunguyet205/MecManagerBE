package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

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
}
