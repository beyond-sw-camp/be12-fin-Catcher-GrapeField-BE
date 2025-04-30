package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostRecommend;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRecommendRepository extends JpaRepository<PostRecommend,Long> {
  Optional<PostRecommend> findByUserAndPost(User user, Post post);

  Integer countByPostAndIsRecommendedTrue(Post post);
}
