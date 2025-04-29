package com.example.grapefield.notification.model.request;

import com.example.grapefield.notification.model.entity.*;
import com.example.grapefield.user.model.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationReq {
  private Long userIdx;
  private Long eventInterestIdx;    // 관련 이벤트 관심 ID
  private Long personalScheduleIdx; // 관련 개인 일정 ID
  private NotificationType notificationType;
  private ScheduleType scheduleType;
  private LocalDateTime notificationTime;  // 알림 시간

  // DTO를 엔티티로 변환
  public ScheduleNotification toEntity(User user,
                                       EventsInterest eventsInterest,
                                       PersonalSchedule personalSchedule) {
    return ScheduleNotification.builder()
        .user(user)
        .eventsInterest(eventsInterest)
        .personalSchedule(personalSchedule)
        .isRead(false)
        .notificationTime(notificationTime)
        .notificationType(notificationType)
        .scheduleType(scheduleType)
        .build();
  }
}
