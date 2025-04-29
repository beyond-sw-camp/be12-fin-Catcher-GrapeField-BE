package com.example.grapefield.user.model.entity;

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
public class UserSanctionHistory { //유저 제재 이력 로그, 기록용
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;
  @ManyToOne
  @JoinColumn(name = "user_idx")
  private User user;
  private String reason;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private LocalDateTime createdAt; //생성일(감사 로그용)
}
