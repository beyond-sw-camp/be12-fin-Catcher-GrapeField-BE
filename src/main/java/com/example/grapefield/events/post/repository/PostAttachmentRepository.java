package com.example.grapefield.events.post.repository;

import com.example.grapefield.events.post.model.entity.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostAttachmentRepository extends JpaRepository<PostAttachment, Long> {
}
