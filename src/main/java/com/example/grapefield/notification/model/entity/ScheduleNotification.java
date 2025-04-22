package com.example.grapefield.notification.model.entity;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.user.model.entity.User;
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

  private LocalDateTime notificationTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType notificationType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ScheduleType scheduleType; // EVENTS_INTEREST or PERSONAL_SCHEDULE
}
