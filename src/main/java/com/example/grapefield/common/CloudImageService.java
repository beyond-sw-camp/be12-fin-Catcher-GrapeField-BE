package com.example.grapefield.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
//@Service
public class CloudImageService implements ImageService {

  private final S3Client s3Client;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucketName;

  public String userProfileUpload(MultipartFile file) {
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String fileName = date + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();

    try {
      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(fileName)
              .contentType(file.getContentType())
              .build(),
          RequestBody.fromBytes(file.getBytes())
      );
    } catch (IOException e) {
      throw new RuntimeException("S3 업로드 실패: " + e.getMessage(), e);
    }

    return "/" + fileName;
  }

  @Override
  public List<String> upload(MultipartFile[] files) {
    List<String> paths = new ArrayList<>();
    for (MultipartFile file : files) {
      paths.add(userProfileUpload(file)); // 단일 업로드 재활용
    }
    return paths;
  }
}