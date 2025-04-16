package com.example.grapefield.events.post;

import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostComment;
import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.post.model.request.CommentRegisterReq;
import com.example.grapefield.events.post.model.response.CommentListResp;
import com.example.grapefield.events.post.repository.PostCommentRepository;
import com.example.grapefield.events.post.repository.PostRepository;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService{
  private final PostCommentRepository commentRepository;
  private final PostRepository postRepository;
  public Page<CommentListResp> getCommentList(Long idx, Pageable pageable, User user) {
      return commentRepository.findCommentList(idx, pageable, user);
  }

  public Long registerComment(CommentRegisterReq request, User user) {
    Post post = postRepository.findById(request.getPostIdx()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글입니다."));
    //댓글 저장
    PostComment comment = PostComment.builder()
        .content(request.getContent())
        .post(post)
        .user(user)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    PostComment saveComment = commentRepository.save(comment);
    return saveComment.getIdx();
  }
}
