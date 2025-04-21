package com.example.grapefield.events.post;

import com.example.grapefield.common.ImageService;
import com.example.grapefield.events.post.model.entity.Board;
import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostAttachment;
import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.post.model.request.PostRegisterReq;
import com.example.grapefield.events.post.model.response.PostDetailResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.events.post.repository.BoardRepository;
import com.example.grapefield.events.post.repository.PostAttachmentRepository;
import com.example.grapefield.events.post.repository.PostRepository;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final PostAttachmentRepository postAttachmentRepository;
  private final BoardRepository boardRepository;
  private final ImageService imageService;

  public Page<PostListResp> getPostList(User user, Long boardIdx, Pageable pageable, String type) {
    PostType postType = PostType.valueOf(type);
      return postRepository.findPostList(boardIdx, pageable, postType, user);
  }

  public PostDetailResp getPostDetail(Long idx, User user) {
    //TODO: 조회수 증가 로직 추가
    return postRepository.findPostDetail(idx, user);
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



//  public PostDetailResp getPostDetail(Long postIdx, User currentUser){
//    Post post = postRepository.findById(postIdx)
//        .orElseThrow();
//    boolean isAuthor = post.getUser().getIdx().equals(currentUser.getIdx());
//    return PostDetailResp.from(post, isAuthor);
//  }
}
