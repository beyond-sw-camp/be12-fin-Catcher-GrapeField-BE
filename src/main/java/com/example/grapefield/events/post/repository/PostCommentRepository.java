package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>, PostCommentCustomRepository {
}
