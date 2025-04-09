package com.example.grapefield.events.post.model.entity;

import com.example.grapefield.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String title;
    private String content;
    private int viewCnt;
    private Boolean isPinned;
    @Enumerated(EnumType.STRING)
    private PostType postType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isVisible;

    @ManyToOne
    @JoinColumn(name = "board_idx")
    private Board board;
    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<PostComment> commentList;

    @OneToMany(mappedBy = "post")
    private List<PostAttachment> attachmentList;

    @OneToMany(mappedBy = "post")
    private List<PostRecommend> recommendList;

    public void incrementViewCount() {
        this.viewCnt++;
    }
}
