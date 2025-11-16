package com.example.mecManager.controller;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.PrescriptionDTO;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.repository.PrescriptionRepository;
import com.example.mecManager.repository.PrescriptionRepositoryJdbc;
import com.example.mecManager.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.URL.API_URL+"/prescription")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping("/searchByDTO")
    public ResponseEntity<ResponseObject> searchByDTO(@RequestBody PrescriptionDTO prescriptionDTO) {
        ResponseObject responseObject = prescriptionService.findByDTO(prescriptionDTO);
        if(responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)){
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
        }
    }

}
