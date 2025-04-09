package com.example.grapefield.events.post.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_idx")
    private Post post;

}
