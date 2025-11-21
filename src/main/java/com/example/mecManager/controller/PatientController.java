package com.example.mecManager.controller;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.URL.API_URL + "/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Get patient detail by ID
     * @param patientId - ID of the patient
     * @return Patient detail information
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<ResponseObject> getPatientById(@PathVariable Long patientId) {
        ResponseObject responseObject = patientService.getPatientById(patientId);
        
        if (responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }

    /**
     * Get all patients with pagination
     * @param page - Page number (default: 1)
     * @param pageSize - Number of items per page (default: 10)
     * @return List of patients with pagination info
     */
    @GetMapping("/list")
    public ResponseEntity<ResponseObject> getAllPatients(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        
        ResponseObject responseObject = patientService.getAllPatients(page, pageSize);
        
        if (responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }

    /**
     * Delete patient by ID
     * @param patientId - ID of the patient to delete
     * @param deletedBy - ID of the user performing the deletion
     * @return Response object with deletion status
     */
    @DeleteMapping("/delete/{patientId}")
    public ResponseEntity<ResponseObject> deletePatient(
            @PathVariable Long patientId,
            @RequestParam Long deletedBy) {
        
        ResponseObject responseObject = patientService.deletePatient(patientId, deletedBy);
        
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
