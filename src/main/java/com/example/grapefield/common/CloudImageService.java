package com.example.grapefield.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudImageService implements ImageService {
  private final S3Client s3Client;
  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucketName;

  // 파일명에 사용할 수 없는 문자 제거
  private String sanitizeFileName(String name) {
    return name.replaceAll("[\\\\/:*?\"<>|]", "_");
  }

  // 파일 경로 생성 메서드
  private String createFilePath(String... pathSegments) {
    StringBuilder pathBuilder = new StringBuilder("images");
    for (String segment : pathSegments) {
      pathBuilder.append("/").append(segment);
    }
    return pathBuilder.toString();
  }

  // 실제 S3에 파일 업로드
  private String uploadToS3(MultipartFile file, String fullPath) throws IOException {
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fullPath)
            .contentType(file.getContentType())
            .build(),
        RequestBody.fromBytes(file.getBytes())
    );
    return fullPath;
  }

  @Override
  public String userProfileUpload(MultipartFile file) {
    if (file.isEmpty()) {
      throw new RuntimeException("빈 파일은 업로드할 수 없습니다.");
    }

    try {
      // 날짜와 랜덤 UUID를 포함한 파일명 생성
      String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
      String originalFilename = sanitizeFileName(file.getOriginalFilename());
      String uploadFilename = date + "_" + UUID.randomUUID() + "_" + originalFilename;

      // 파일 경로 생성
      String fullPath = createFilePath("userprofile", uploadFilename);

      // S3에 업로드
      return uploadToS3(file, fullPath);
    } catch (IOException e) {
      throw new RuntimeException("S3 업로드 실패: " + e.getMessage(), e);
    }
  }

  @Override
  public List<String> upload(MultipartFile[] files) {
    List<String> uploadedPaths = new ArrayList<>();

    for (MultipartFile file : files) {
      if (!file.isEmpty()) {
        uploadedPaths.add(userProfileUpload(file));
      }
    }

    return uploadedPaths;
  }

  @Override
  public List<String> postAttachmentsUpload(String boardTitle, MultipartFile[] files) {
    if (files == null || files.length == 0) {
      return new ArrayList<>();
    }

    try {
      List<String> uploadedPaths = new ArrayList<>();

      // 게시판 제목을 안전한 디렉토리명으로 변환
      String sanitizedBoardTitle = sanitizeFileName(boardTitle).toLowerCase();
      String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          // 원본 파일명 정제
          String originalFilename = sanitizeFileName(file.getOriginalFilename());
          String uploadFilename = date + "_" + UUID.randomUUID() + "_" + originalFilename;

          // 파일 경로 생성
          String fullPath = createFilePath(sanitizedBoardTitle, "post", uploadFilename);

          // S3에 업로드
          String uploadedPath = uploadToS3(file, fullPath);
          uploadedPaths.add(uploadedPath);
        }
      }

      return uploadedPaths;
    } catch (IOException e) {
      throw new RuntimeException("S3 다중 파일 업로드 실패: " + e.getMessage(), e);
    }
  }

  @Override
  public boolean deleteFile(String filePath) {
    try {
      // 파일 경로에서 "/"를 제거하고 S3 키로 사용
      String key = filePath.startsWith("/") ? filePath.substring(1) : filePath;

      // S3에서 객체 삭제
      s3Client.deleteObject(
          DeleteObjectRequest.builder()
              .bucket(bucketName)
              .key(key)
              .build()
      );

      return true;
    } catch (Exception e) {
      System.err.println("S3 파일 삭제 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}