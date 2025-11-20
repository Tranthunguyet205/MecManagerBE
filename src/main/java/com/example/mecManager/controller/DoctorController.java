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
}
