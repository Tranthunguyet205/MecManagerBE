package com.example.mecManager.controller;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.MedicineInfoDTO;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.URL.API_URL + "/medicine")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createMedicine(
            @RequestBody MedicineInfoDTO medicineInfoDTO,
            @RequestParam Long createdBy) {
        
        ResponseObject responseObject = medicineService.createMedicine(medicineInfoDTO, createdBy);
        
        if (responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
        } else if (responseObject.getStatus().equals(AppConstants.STATUS.BAD_REQUEST)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }

    @DeleteMapping("/delete/{medicineId}")
    public ResponseEntity<ResponseObject> deleteMedicine(
            @PathVariable Long medicineId,
            @RequestParam Long deletedBy) {
        
        ResponseObject responseObject = medicineService.deleteMedicine(medicineId, deletedBy);
        
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
