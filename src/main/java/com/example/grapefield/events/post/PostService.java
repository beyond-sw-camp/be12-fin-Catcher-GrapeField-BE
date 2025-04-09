package com.example.grapefield.events.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
//  public PostDetailResp getPostDetail(Long postIdx, User currentUser){
//    Post post = postRepository.findById(postIdx)
//        .orElseThrow();
//    boolean isAuthor = post.getUser().getIdx().equals(currentUser.getIdx());
//    return PostDetailResp.from(post, isAuthor);
//  }
}
