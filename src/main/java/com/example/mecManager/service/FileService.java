package com.example.mecManager.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  String uploadFile(String bucket, Long id, String fileName, MultipartFile file);

  void deleteFile(String bucket, Long id, String fileName);

  void deleteAllFiles(String bucket, Long id);
}
