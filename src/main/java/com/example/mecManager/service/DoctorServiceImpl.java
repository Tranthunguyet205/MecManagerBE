package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.DocInfo;
import com.example.mecManager.model.DocInfoDTO;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.model.User;
import com.example.mecManager.repository.DocInfoRepository;
import com.example.mecManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DocInfoRepository docInfoRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseObject createDoctorInfo(DocInfoDTO docInfoDTO, Long createdBy) {
        try {
            // Validate user exists
            Optional<User> userOptional = userRepository.findById(docInfoDTO.getUserId());
            if (!userOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "User not found", null);
            }

            User user = userOptional.get();

            // Check if doctor info already exists for this user
            DocInfo existingDocInfo = docInfoRepository.findByUserId(docInfoDTO.getUserId());
            if (existingDocInfo != null) {
                return new ResponseObject(AppConstants.STATUS.ALREADY_EXISTS, "Doctor information already exists for this user", null);
            }

            // Validate creator exists
            Optional<User> creatorOptional = userRepository.findById(createdBy);
            if (!creatorOptional.isPresent()) {
                return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Creator user not found", null);
            }

            User creator = creatorOptional.get();

            // Create new DocInfo
            DocInfo docInfo = new DocInfo();
            docInfo.setUser(user);
            docInfo.setFullName(docInfoDTO.getFullName());
            docInfo.setDob(docInfoDTO.getDob());
            docInfo.setPhone(docInfoDTO.getPhone());
            docInfo.setCccd(docInfoDTO.getCccd());
            docInfo.setCccdIssueDate(docInfoDTO.getCccdIssueDate());
            docInfo.setCccdIssuePlace(docInfoDTO.getCccdIssuePlace());
            docInfo.setCurrentAddress(docInfoDTO.getCurrentAddress());
            docInfo.setEmail(docInfoDTO.getEmail());
            docInfo.setPracticeCertificateNo(docInfoDTO.getPracticeCertificateNo());
            docInfo.setPracticeCertificateIssueDate(docInfoDTO.getPracticeCertificateIssueDate());
            docInfo.setPracticeCertificateIssuePlace(docInfoDTO.getPracticeCertificateIssuePlace());
            docInfo.setLicenseNo(docInfoDTO.getLicenseNo());
            docInfo.setLicenseIssueDate(docInfoDTO.getLicenseIssueDate());
            docInfo.setLicenseIssuePlace(docInfoDTO.getLicenseIssuePlace());
            docInfo.setCreatedAt(new Date());
            docInfo.setUserCreateBy(creator);

            DocInfo savedDocInfo = docInfoRepository.save(docInfo);

            return new ResponseObject(AppConstants.STATUS.SUCCESS, "Doctor information created successfully", savedDocInfo);

        } catch (Exception e) {
            return new ResponseObject(AppConstants.STATUS.ERROR, "Error creating doctor information: " + e.getMessage(), null);
        }
    }
}
