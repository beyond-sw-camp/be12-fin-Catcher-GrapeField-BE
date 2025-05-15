package com.example.grapefield.events.post.model.entity;

import com.example.grapefield.user.model.entity.User;
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
public class PostScrap {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "post_idx")
  private Post post;

  @ManyToOne
  @JoinColumn(name = "user_idx")
  private User user;

  @Column(name = "is_scrapped", columnDefinition = "BOOLEAN DEFAULT false")
  private Boolean isScrapped; // 스크랩 상태를 나타내는 필드(토글형으로 구현하기 위해서)

  // 상태 토글을 위한 메서드
  public void toggleScrapped() {
    this.isScrapped = !Boolean.TRUE.equals(this.isScrapped);
  }
}
