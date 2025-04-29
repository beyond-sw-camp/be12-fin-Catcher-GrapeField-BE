package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostAttachmentRepository extends JpaRepository<PostAttachment, Long> {
  // 특정 게시글과 파일 URL로 첨부파일 삭제
  void deleteByPostAndFileUrl(Post post, String fileUrl);

  // 특정 게시글의 모든 첨부파일 조회
  List<PostAttachment> findByPost(Post post);

  // 특정 게시글의 모든 첨부파일 삭제
  void deleteByPost(Post post);

  PostAttachment findByPostAndFileUrl(Post post, String fileUrl);
}
