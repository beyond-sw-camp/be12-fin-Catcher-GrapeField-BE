package com.example.grapefield.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LocalImageService implements ImageService {
  @Value("${file.upload.path}")
  private String defaultUploadPath;

  //디렉토리가 없으면 생성
  private String makeDir(){
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String uploadPath = defaultUploadPath + File.separator + date; // OS에 맞는 경로 구분자 사용
    File uploadDir = new File(uploadPath);
    if(!uploadDir.exists()){
      uploadDir.mkdirs();
    }
    return uploadPath;  // "/" 제거하여 경로 중복 문제 해결
  }

  @Override
  public String userProfileUpload(MultipartFile file) {
    //폴더가 없으면 생성
    String uploadPath = defaultUploadPath + File.separator + "userProfile";
    File uploadDir = new File(uploadPath);
    if(!uploadDir.exists()){
      uploadDir.mkdirs();
    }

    String originalFilename = file.getOriginalFilename();
    String uploadFilePath = UUID.randomUUID().toString() + "_" + originalFilename;  // 파일명에 UUID 추가
    File uploadFile = new File(uploadPath, uploadFilePath);  // File 생성 방식 개선 (OS 호환성)
    try {
      file.transferTo(uploadFile);
    } catch (IOException e) {
      throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
    }

    return uploadFilePath;
  }

  @Override
  public List<String> upload(MultipartFile[] files) {
    List<String> uploadFilePaths = new ArrayList<>();
    String uploadPath = makeDir();  // 수정된 `makeDir()` 사용

    for (MultipartFile file : files) {
      String originalFilename = file.getOriginalFilename();
      String uploadFilePath = UUID.randomUUID().toString() + "_" + originalFilename;  // 파일명에 UUID 추가

      File uploadFile = new File(uploadPath, uploadFilePath);  // File 생성 방식 개선 (OS 호환성)
      try {
        file.transferTo(uploadFile);
        uploadFilePaths.add(uploadFile.getAbsolutePath());  // 절대 경로 저장
      } catch (IOException e) {
        throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
      }
    }
    return uploadFilePaths;
  }
}