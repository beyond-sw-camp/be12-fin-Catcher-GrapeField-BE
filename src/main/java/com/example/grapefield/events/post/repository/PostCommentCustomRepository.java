package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.response.CommentListResp;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCommentCustomRepository {
  Page<CommentListResp> findCommentList(Long idx, Pageable pageable, User user);
}
