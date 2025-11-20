package com.example.mecManager.controller;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.DocInfoDTO;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.URL.API_URL + "/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createDoctorInfo(
            @RequestBody DocInfoDTO docInfoDTO,
            @RequestParam Long createdBy) {
        
        ResponseObject responseObject = doctorService.createDoctorInfo(docInfoDTO, createdBy);
        
        if (responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.ALREADY_EXISTS)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }

    /**
     * Get doctor detail by ID
     * @param doctorId - ID of the doctor
     * @return Doctor detail information
     */
    @GetMapping("/{doctorId}")
    public ResponseEntity<ResponseObject> getDoctorById(@PathVariable Long doctorId) {
        ResponseObject responseObject = doctorService.getDoctorById(doctorId);
        
        if (responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }

    /**
     * Get all doctors with pagination
     * @param page - Page number (default: 1)
     * @param pageSize - Number of items per page (default: 10)
     * @return List of doctors with pagination info
     */
    @GetMapping("/list")
    public ResponseEntity<ResponseObject> getAllDoctors(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        
        ResponseObject responseObject = doctorService.getAllDoctors(page, pageSize);
        
        if (responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }

    /**
     * Search doctors by practice certificate number or license number
     * @param practiceCertificateNo - Practice certificate number (optional)
     * @param licenseNo - License number (optional)
     * @param page - Page number (default: 1)
     * @param pageSize - Number of items per page (default: 10)
     * @return Doctor information or list of doctors
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchDoctors(
            @RequestParam(required = false) String practiceCertificateNo,
            @RequestParam(required = false) String licenseNo,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        
        ResponseObject responseObject = doctorService.searchDoctors(practiceCertificateNo, licenseNo, page, pageSize);
        
        if (responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }

    /**
     * Delete doctor by ID
     * @param doctorId - ID of the doctor to delete
     * @param deletedBy - ID of the user performing the deletion
     * @return Response object with deletion status
     */
    @DeleteMapping("/delete/{doctorId}")
    public ResponseEntity<ResponseObject> deleteDoctor(
            @PathVariable Long doctorId,
            @RequestParam Long deletedBy) {
        
        ResponseObject responseObject = doctorService.deleteDoctor(doctorId, deletedBy);
        
        if (responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.BAD_REQUEST)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }
}
