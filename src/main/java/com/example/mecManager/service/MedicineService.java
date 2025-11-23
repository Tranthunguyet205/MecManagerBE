package com.example.mecManager.service;

import com.example.mecManager.model.MedicineInfoDTO;
import com.example.mecManager.model.ResponseObject;

public interface MedicineService {
    ResponseObject createMedicine(MedicineInfoDTO medicineInfoDTO, Long createdBy);
    ResponseObject deleteMedicine(Long medicineId, Long deletedBy);
}
