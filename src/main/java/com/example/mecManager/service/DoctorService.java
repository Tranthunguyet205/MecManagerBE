package com.example.mecManager.service;

import com.example.mecManager.model.DocInfoDTO;
import com.example.mecManager.model.ResponseObject;

public interface DoctorService {
    ResponseObject createDoctorInfo(DocInfoDTO docInfoDTO, Long createdBy);
    ResponseObject getDoctorById(Long doctorId);
    ResponseObject getAllDoctors(Integer page, Integer pageSize);
    ResponseObject searchDoctors(String practiceCertificateNo, String licenseNo, Integer page, Integer pageSize);
    ResponseObject deleteDoctor(Long doctorId, Long deletedBy);
}
