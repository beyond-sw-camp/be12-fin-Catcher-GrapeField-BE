package com.example.grapefield.notification.model.entity;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalSchedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;
  private String title;
  private String description;
  private Boolean isNotify;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDate createdAt;
  private LocalDate updatedAt;

  @ManyToOne
  @JoinColumn(name = "user_idx")
  private User user;
}
