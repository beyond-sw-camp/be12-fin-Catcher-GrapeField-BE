package com.example.grapefield.events.post;

import com.example.grapefield.common.PageResponse;
import com.example.grapefield.events.post.model.entity.Post;
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

  public Page<PostListResp> getPostList(User user, Long boardIdx, Pageable pageable) {
    boolean isAdmin = user != null && user.getRole() == UserRole.ROLE_ADMIN;
    if(isAdmin){
      return postRepository.findPostListForAdmin(boardIdx, pageable);
    }else{
      return postRepository.findPostList(boardIdx, pageable);
    }
  }



//  public PostDetailResp getPostDetail(Long postIdx, User currentUser){
//    Post post = postRepository.findById(postIdx)
//        .orElseThrow();
//    boolean isAuthor = post.getUser().getIdx().equals(currentUser.getIdx());
//    return PostDetailResp.from(post, isAuthor);
//  }
}
