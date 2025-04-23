package com.example.grapefield.notification.model.entity;

import com.example.grapefield.user.model.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleNotification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_idx", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "events_interest_idx")
  private EventsInterest eventsInterest;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "personal_schedule_idx")
  private PersonalSchedule personalSchedule;

  private Boolean isRead;

  @Builder.Default
  private Boolean isVisible = true;

  private LocalDateTime notificationTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private NotificationType notificationType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ScheduleType scheduleType; // EVENTS_INTEREST or PERSONAL_SCHEDULE

  // 읽음 표시 메서드
  public void markAsRead() {
    this.isRead = true;
  }
  public void hide() { this.isVisible = false; }
}
