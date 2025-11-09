package com.example.mecManager.controller;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.URL.API_URL+"/PreScription")
@RequiredArgsConstructor
public class PrescriptionController {


}
