package com.example.mecManager.service;

import com.example.mecManager.model.ResponseObject;

public interface PrescriptionService {
    ResponseObject getPrescriptionByCode(String code);
    ResponseObject getPrescriptionById(Long id);
}
