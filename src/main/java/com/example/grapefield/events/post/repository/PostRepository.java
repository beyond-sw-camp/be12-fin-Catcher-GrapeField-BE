package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
}

