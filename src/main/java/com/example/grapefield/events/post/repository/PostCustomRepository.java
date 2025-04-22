package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.post.model.response.CommunityPostListResp;
import com.example.grapefield.events.post.model.response.PostDetailResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.events.post.model.response.PostSearchListResp;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostCustomRepository {
  Page<PostListResp> findPostList(Long boardIdx, Pageable pageable, PostType postType , User user);
  Page<CommunityPostListResp> findPostLists(User user, Pageable pageable, String type, String orderBy);

  PostDetailResp findPostDetail(Long idx, User user);

  //검색 관련
  Page<PostSearchListResp> findPostsByKeyword(String keyword, Pageable pageable, User user);
  Page<PostSearchListResp> findPostsByKeywordAnd(List<String> keywords, Pageable pageable, User user);
}
