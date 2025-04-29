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
public class UserBlock { //현재 활성화된 차단 정보
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;
  @ManyToOne
  @JoinColumn(name = "user_idx")
  private User user;
  private String reason;
  private LocalDateTime blockDate;
  private LocalDateTime unblockDate;
  //유저가 영구차단되었다면 user_block에서 해당 유저와 관련 데이터는 전부 지워도 됨
  private Boolean isActive; //현재 차단 여부
  private LocalDateTime createdAt;
}
