package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostScrap;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
  Optional<PostScrap> findByUserAndPost(User user, Post post);
}
