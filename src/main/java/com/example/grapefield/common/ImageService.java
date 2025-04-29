package com.example.grapefield.common;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

//local에서 cloud로 확장 생각, image service를 interface로 구현
public interface ImageService {
  String userProfileUpload(MultipartFile file);
  List<String> upload(MultipartFile[] files);
  List<String> postAttachmentsUpload(String boardTitle, MultipartFile[] files);
  boolean deleteFile(String filePath);
}
