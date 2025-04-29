package com.example.grapefield.events.post;

import com.example.grapefield.common.ImageService;
import com.example.grapefield.events.post.model.entity.*;
import com.example.grapefield.events.post.model.request.PostRegisterReq;
import com.example.grapefield.events.post.model.request.PostUpdateReq;
import com.example.grapefield.events.post.model.response.CommunityPostListResp;
import com.example.grapefield.events.post.model.response.PostDetailResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.events.post.repository.BoardRepository;
import com.example.grapefield.events.post.repository.PostAttachmentRepository;
import com.example.grapefield.events.post.repository.PostRecommendRepository;
import com.example.grapefield.events.post.repository.PostRepository;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final PostAttachmentRepository postAttachmentRepository;
  private final BoardRepository boardRepository;
  private final ImageService imageService;
  private final PostRecommendRepository postRecommendRepository;

  public Page<PostListResp> getPostList(User user, Long boardIdx, Pageable pageable, String type) {
    PostType postType = PostType.valueOf(type);
      return postRepository.findPostList(boardIdx, pageable, postType, user);
  }

  public Page<CommunityPostListResp> getPostLists(User user, Pageable pageable, String type, String orderBy) {

    return postRepository.findPostLists(user, pageable, type, orderBy);
  }

  public PostDetailResp getPostDetail(Long idx, User user) {
    return postRepository.findPostDetail(idx, user);
  }

  @Transactional
  public Integer updateViewCount(Long postIdx) {
    // 게시글 조회
    Post post = postRepository.findById(postIdx)
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + postIdx));
    post.incrementViewCount();
    postRepository.save(post);
    return post.getViewCnt();
  }

  /**
   * 게시글 등록 및 첨부파일 저장
   * @param request 게시글 등록 요청 DTO
   * @param files 첨부 파일 배열
   * @param user 현재 로그인한 사용자
   * @return 등록된 게시글 ID
   */
  @Transactional
  public Long registerPost(PostRegisterReq request, MultipartFile[] files, User user) {
    // 게시판 존재 여부 확인
    Board board = boardRepository.findById(request.getBoardIdx())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

    // 게시글 생성 및 저장
    Post post = Post.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .postType(request.getPostType())
        .isVisible(true)
        .isPinned(false)
        .viewCnt(0)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .user(user)
        .board(board)
        .build();
    Post savedPost = postRepository.save(post);
    // 첨부파일이 있는 경우에만 처리
    if (files != null && files.length > 0) {
      savePostAttachments(savedPost, board.getTitle(), files);
    }
    return savedPost.getIdx();
  }

  /**
   * 게시글 첨부파일 저장
   * @param post       저장된 게시글 엔티티
   * @param boardTitle 게시판 제목 (폴더명 용도)
   * @param files      첨부 파일 배열
   */
  private void savePostAttachments(Post post, String boardTitle, MultipartFile[] files) {
    // 비어있는 파일은 처리하지 않음
    if (files == null || files.length == 0) { return; }
    // 게시판 제목을 사용해 첨부파일 업로드
    List<String> filePaths = imageService.postAttachmentsUpload(boardTitle, files);
    // 업로드된 파일 정보를 DB에 저장
    List<PostAttachment> attachments = new ArrayList<>();
    for (int i = 0; i < files.length; i++) {
      MultipartFile file = files[i];
      // 빈 파일 건너뛰기
      if (file.isEmpty()) { continue; }
      String filePath = filePaths.get(i);
      PostAttachment attachment = PostAttachment.builder()
          .fileUrl(filePath)
          .fileName(file.getOriginalFilename())
          .fileSize(file.getSize())
          .fileType(file.getContentType())
          .createdAt(LocalDateTime.now())
          .post(post)
          .build();
      attachments.add(attachment);
    }
    // 모든 첨부파일 정보를 한 번에 저장
    if (!attachments.isEmpty()) { postAttachmentRepository.saveAll(attachments); }
  }

  private boolean hasPermission(Post post, User user) {
    if (post == null || user == null) { return false; }

    return Objects.equals(post.getUser().getIdx(), user.getIdx()) || user.getRole() == UserRole.ROLE_ADMIN;
  }

  public boolean deletePost(Long postIdx, User user) {
    try {
      Post post = postRepository.findById(postIdx)
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
      if (!hasPermission(post, user)) { return false; }
      Post updatedPost = post.toBuilder().isVisible(false).build();
      postRepository.save(updatedPost);
      return true;
    } catch (IllegalArgumentException e) {
      System.err.println("게시글 삭제 중 오류: " + e.getMessage());
      return false;
    }
  }

  @Transactional
  public Long updatePost(Long postIdx, PostUpdateReq request, MultipartFile[] files, User user) {

    try {
      // 게시판 존재 여부 확인
      Board board = boardRepository.findById(request.getBoardIdx()).orElseThrow(() -> { return new IllegalArgumentException("존재하지 않는 게시판입니다."); });

      // 게시글 존재 여부 확인
      Post post = postRepository.findById(postIdx).orElseThrow(() -> { return new IllegalArgumentException("존재하지 않는 게시글입니다."); });

      // 권한 확인 (작성자 또는 관리자만 수정 가능)
      if (!hasPermission(post, user)) {
        throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
      }
      // 게시글 정보 업데이트
      Post updatedPost = null;
      try {
        updatedPost = post.toBuilder()
            .title(request.getTitle())
            .content(request.getContent())
            .postType(request.getPostType())
            .updatedAt(LocalDateTime.now())
            .board(board)
            .build();
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }

      // 게시글 저장
      Post savedPost = null;
      try {
        savedPost = postRepository.save(updatedPost);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }

      // 삭제할 이미지 처리
      try {
        removePostAttachments(savedPost, request.getRemovedImagePaths());
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }

      // 새 이미지 파일 처리
      try {
        savePostAttachments(savedPost, board.getTitle(), files);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
      return savedPost.getIdx();
    } catch (Exception e) {
      e.printStackTrace();
      throw e; // 상위로 예외 전파
    }
  }

  //게시글 첨부파일 삭제
  private void removePostAttachments(Post post, List<String> filePaths) {
    if (filePaths == null || filePaths.isEmpty()) { return; }

    for (String filePath : filePaths) {
      try {
        // DB에서 해당 경로의 첨부파일 정보 찾기
        PostAttachment attachment = postAttachmentRepository.findByPostAndFileUrl(post, filePath);

        if (attachment != null) {
          // DB에서 먼저 삭제
          postAttachmentRepository.delete(attachment);

          // 실제 파일 삭제
          try {
            imageService.deleteFile(filePath);
          } catch (Exception e) {
            // 파일 삭제 실패는 로그만 남기고 진행 (중요하지 않은 오류로 취급)
            System.err.println("파일 삭제 실패: " + filePath + ", 오류: " + e.getMessage());
          }
        } else {
          System.err.println("삭제할 첨부파일 정보를 찾을 수 없음: " + filePath);
        }
      } catch (Exception e) {
        throw new RuntimeException("첨부파일 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
      }
    }
  }

  public Integer postRecommend(Long idx, User user) {
    Post post = postRepository.findById(idx).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다"));
    PostRecommend recommend = postRecommendRepository.findByUserAndPost(user, post)
        .orElse(PostRecommend.builder()
            .user(user)
            .post(post)
            .createdAt(LocalDateTime.now())
            .build());

    // 추천 여부 토글
    recommend.toggleRecommendation();
    postRecommendRepository.save(recommend);

    return postRecommendRepository.countByPostAndIsRecommendedTrue(post);
  }
}
