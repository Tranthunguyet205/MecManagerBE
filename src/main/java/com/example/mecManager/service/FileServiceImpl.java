package com.example.mecManager.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final MinioClient minioClient;

  @Value("${minio.url}")
  private String minioUrl;

  @Override
  public String uploadFile(String bucket, Long id, String fileName, MultipartFile file) {
    try {
      ensureBucketExists(bucket);

      String objectName = id + "/" + fileName;
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucket)
              .object(objectName)
              .stream(file.getInputStream(), file.getSize(), -1)
              .contentType(file.getContentType())
              .build());

      String url = minioUrl + "/" + bucket + "/" + objectName;
      log.info("File uploaded: {}", url);
      return url;
    } catch (Exception e) {
      log.error("Error uploading file: {}", e.getMessage());
      throw new RuntimeException("Lỗi tải file lên: " + e.getMessage());
    }
  }

  @Override
  public void deleteFile(String bucket, Long id, String fileName) {
    try {
      String objectName = id + "/" + fileName;
      minioClient.removeObject(RemoveObjectArgs.builder()
          .bucket(bucket)
          .object(objectName)
          .build());
      log.info("File deleted: {}", objectName);
    } catch (Exception e) {
      log.warn("Error deleting file: {}", e.getMessage());
    }
  }

  @Override
  public void deleteAllFiles(String bucket, Long id) {
    try {
      String prefix = id + "/";
      var results = minioClient.listObjects(
          ListObjectsArgs.builder()
              .bucket(bucket)
              .prefix(prefix)
              .recursive(true)
              .build());

      List<DeleteObject> deleteObjects = new LinkedList<>();
      for (var result : results) {
        deleteObjects.add(new DeleteObject(result.get().objectName()));
      }

      if (!deleteObjects.isEmpty()) {
        minioClient.removeObjects(RemoveObjectsArgs.builder()
            .bucket(bucket)
            .objects(deleteObjects)
            .build());
      }
    } catch (Exception e) {
      log.warn("Error deleting all files: {}", e.getMessage());
    }
  }

  private void ensureBucketExists(String bucket) throws Exception {
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      log.info("Bucket created: {}", bucket);
      
      // Set public read policy for the bucket
      String policy = "{\n" +
        "  \"Version\": \"2012-10-17\",\n" +
        "  \"Statement\": [\n" +
        "    {\n" +
        "      \"Effect\": \"Allow\",\n" +
        "      \"Principal\": \"*\",\n" +
        "      \"Action\": \"s3:GetObject\",\n" +
        "      \"Resource\": \"arn:aws:s3:::" + bucket + "/*\"\n" +
        "    }\n" +
        "  ]\n" +
        "}";
      minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
          .bucket(bucket)
          .config(policy)
          .build());
      log.info("Bucket policy set to public read for: {}", bucket);
    }
  }
}
