package com.example.mecManager.service;

import com.example.mecManager.model.DocInfoDTO;
import com.example.mecManager.model.ResponseObject;

public interface DoctorService {
    ResponseObject createDoctorInfo(DocInfoDTO docInfoDTO, Long createdBy);
}
