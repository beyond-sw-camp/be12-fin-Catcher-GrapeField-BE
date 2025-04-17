package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.post.model.response.PostDetailResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {
  Page<PostListResp> findPostList(Long boardIdx, Pageable pageable, PostType postType);
  Page<PostListResp> findPostListForAdmin(Long boardIdx, Pageable pageable, PostType postType);
  PostDetailResp findPostDetail(Long idx, User user);
}
