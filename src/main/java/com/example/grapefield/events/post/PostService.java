package com.example.grapefield.events.post;

import com.example.grapefield.common.PageResponse;
import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.post.model.response.PostDetailResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.model.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;

  public Page<PostListResp> getPostList(User user, Long boardIdx, Pageable pageable, String type) {
    boolean isAdmin = user != null && user.getRole() == UserRole.ROLE_ADMIN;
    PostType postType = PostType.valueOf(type);
    if(isAdmin){
      return postRepository.findPostListForAdmin(boardIdx, pageable, postType);
    }else{
      return postRepository.findPostList(boardIdx, pageable, postType);
    }
  }

  public PostDetailResp getPostDetail(Long idx, User user) {
    //TODO: 조회수 증가 로직 추가
    return postRepository.findPostDetail(idx, user);
  }


//  public PostDetailResp getPostDetail(Long postIdx, User currentUser){
//    Post post = postRepository.findById(postIdx)
//        .orElseThrow();
//    boolean isAuthor = post.getUser().getIdx().equals(currentUser.getIdx());
//    return PostDetailResp.from(post, isAuthor);
//  }
}
