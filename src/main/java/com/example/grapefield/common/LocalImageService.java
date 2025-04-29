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

  // 범용 디렉토리 생성 메서드
  private String makeDir(String... subDirs) {
    StringBuilder pathBuilder = new StringBuilder(defaultUploadPath);

    for (String dir : subDirs) {
      pathBuilder.append(File.separator).append(dir);
    }

    String path = pathBuilder.toString();
    File directory = new File(path);

    if (!directory.exists()) {
      boolean created = directory.mkdirs();
      if (!created) {
        throw new RuntimeException("디렉토리 생성 실패: " + path);
      }
    }

    return path;
  }

  // DB에 저장할 상대 경로 생성 메서드
  private String makeRelativePath(String... subDirs) {
    StringBuilder pathBuilder = new StringBuilder("images");

    for (String dir : subDirs) {
      pathBuilder.append("/").append(dir); // 웹 경로는 항상 / 사용
    }

    return pathBuilder.toString();
  }

  // 파일명에 사용할 수 없는 문자 제거
  private String sanitizeDirectoryName(String name) {
    // 파일 시스템에서 사용할 수 없는 문자를 제거하거나 대체
    return name.replaceAll("[\\\\/:*?\"<>|]", "_");
  }

  // 실제 파일 시스템에 파일 저장
  private void saveFileToSystem(MultipartFile file, String uploadPath, String filename) {
    if (file.isEmpty()) {
      throw new RuntimeException("빈 파일은 업로드할 수 없습니다.");
    }

    File uploadFile = new File(uploadPath, filename);

    try {
      file.transferTo(uploadFile);
    } catch (IOException e) {
      throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
    }
  }

  @Override
  public String userProfileUpload(MultipartFile file) {
    // 실제 파일 저장 경로
    String uploadPath = makeDir("images", "userprofile");

    // 파일명 생성
    String originalFilename = file.getOriginalFilename();
    String uploadFilename = UUID.randomUUID().toString() + "_" + originalFilename;

    // 파일 저장
    saveFileToSystem(file, uploadPath, uploadFilename);

    // DB에 저장할 상대 경로 반환
    return makeRelativePath("userprofile", uploadFilename);
  }

  @Override
  public List<String> upload(MultipartFile[] files) {
    // 날짜별 디렉토리 생성
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String uploadPath = makeDir("images", date);

    List<String> relativePaths = new ArrayList<>();

    for (MultipartFile file : files) {
      if (file.isEmpty()) continue;

      // 파일명 생성
      String originalFilename = file.getOriginalFilename();
      String uploadFilename = UUID.randomUUID().toString() + "_" + originalFilename;

      // 파일 저장
      saveFileToSystem(file, uploadPath, uploadFilename);

      // DB에 저장할 상대 경로 추가
      String relativePath = makeRelativePath(date, uploadFilename);
      relativePaths.add(relativePath);
    }

    return relativePaths;
  }

  @Override
  public List<String> postAttachmentsUpload(String boardTitle, MultipartFile[] files) {
    // 파일 시스템 경로 생성
    String sanitizedBoardTitle = sanitizeDirectoryName(boardTitle).toLowerCase();
    String uploadPath = makeDir("images", sanitizedBoardTitle, "post");

    List<String> relativePaths = new ArrayList<>();

    for (MultipartFile file : files) {
      if (file.isEmpty()) continue;

      // 파일명 생성
      String originalFilename = file.getOriginalFilename();
      String uploadFilename = UUID.randomUUID().toString() + "_" + originalFilename;

      // 파일 저장
      saveFileToSystem(file, uploadPath, uploadFilename);

      // DB에 저장할 상대 경로 추가
      String relativePath = makeRelativePath(sanitizedBoardTitle, "post", uploadFilename);
      relativePaths.add(relativePath);
    }

    return relativePaths;
  }
}