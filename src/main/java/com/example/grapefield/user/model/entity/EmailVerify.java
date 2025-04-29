package com.example.grapefield.user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailVerify {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;
  private String uuid;

  @ManyToOne
  @JoinColumn(name = "user_idx")
  private User user;
}
