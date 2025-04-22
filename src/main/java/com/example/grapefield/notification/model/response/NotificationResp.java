package com.example.grapefield.notification.model.response;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.notification.model.entity.NotificationType;
import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.notification.model.entity.ScheduleType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResp {
  private Long idx;                    // 알림 ID
  private Long userIdx;                // 사용자 ID
  private String title;               // 알림 제목
  private LocalDateTime notificationTime;  // 알림 예정 시간
  private Boolean isRead;             // 읽음 여부
  private NotificationType notificationType;  // 알림 유형
  private ScheduleType scheduleType;  // 알림 대상 유형

  // 알림 관련 추가 정보
  private EventSummaryResp event;      // 관련 이벤트 정보
  private PersonalScheduleResp personalSchedule;  // 관련 개인 일정 정보

  // 사용자 친화적 시간 표시
  private String formattedTime;       // "10분 전", "1시간 전" 등

  // 알림 엔티티를 DTO로 변환
  public static NotificationResp fromEntity(ScheduleNotification notification) {
    NotificationResp dto = NotificationResp.builder()
        .idx(notification.getIdx())
        .userIdx(notification.getUser().getIdx())
        .notificationType(notification.getNotificationType())
        .scheduleType(notification.getScheduleType())
        .notificationTime(notification.getNotificationTime())
        .isRead(notification.getIsRead())
        .build();

    // 시간 포맷팅
    dto.setFormattedTime(formatTimeAgo(notification.getNotificationTime()));

    // 이벤트 정보 추가
    if (notification.getScheduleType() == ScheduleType.EVENTS_INTEREST &&
        notification.getEventsInterest() != null) {

      Events event = notification.getEventsInterest().getEvents();
      if (event != null) {
        dto.setTitle(event.getTitle());
        dto.setEvent(EventSummaryResp.fromEntity(event));
      }
    }

    // 개인 일정 정보 추가
    if (notification.getScheduleType() == ScheduleType.PERSONAL_SCHEDULE &&
        notification.getPersonalSchedule() != null) {

      PersonalSchedule schedule = notification.getPersonalSchedule();
      if (schedule != null) {
        dto.setTitle(schedule.getTitle());
        dto.setPersonalSchedule(PersonalScheduleResp.fromEntity(schedule));
      }
    }

    return dto;
  }

  // 시간을 사용자 친화적인 형식으로 변환
  private static String formatTimeAgo(LocalDateTime dateTime) {
    if (dateTime == null) {
      return "";
    }

    LocalDateTime now = LocalDateTime.now();
    Duration duration = Duration.between(dateTime, now);

    if (duration.isNegative()) {
      // 미래 시간인 경우
      duration = duration.negated();
      if (duration.toMinutes() < 60) {
        return duration.toMinutes() + "분 후";
      } else if (duration.toHours() < 24) {
        return duration.toHours() + "시간 후";
      } else {
        return duration.toDays() + "일 후";
      }
    } else {
      // 과거 시간인 경우
      if (duration.toMinutes() < 1) {
        return "방금 전";
      } else if (duration.toHours() < 1) {
        return duration.toMinutes() + "분 전";
      } else if (duration.toDays() < 1) {
        return duration.toHours() + "시간 전";
      } else if (duration.toDays() < 7) {
        return duration.toDays() + "일 전";
      } else {
        return DateTimeFormatter.ofPattern("yyyy.MM.dd").format(dateTime);
      }
    }
  }

  // 알림 메시지 생성
  private static String buildMessage(ScheduleNotification notification) {
    if (notification.getScheduleType() == ScheduleType.EVENTS_INTEREST &&
        notification.getEventsInterest() != null) {

      Events event = notification.getEventsInterest().getEvents();
      if (event == null) return "알림이 도착했습니다.";

      switch (notification.getNotificationType()) {
        case START_REMINDER:
          return String.format("오늘은 '%s' 공연/전시 시작일입니다.", event.getTitle());
        case HOUR_REMINDER:
          return String.format("'%s' 공연/전시가 1시간 후에 시작합니다.", event.getTitle());
        case CUSTOM_MESSAGE:
          return String.format("'%s' 관련 소식이 있습니다.", event.getTitle());
        default:
          return "알림이 도착했습니다.";
      }
    } else if (notification.getScheduleType() == ScheduleType.PERSONAL_SCHEDULE &&
        notification.getPersonalSchedule() != null) {

      PersonalSchedule schedule = notification.getPersonalSchedule();
      if (schedule == null) return "알림이 도착했습니다.";

      return String.format("오늘은 '%s' 일정이 있는 날입니다.", schedule.getTitle());
    }

    return "알림이 도착했습니다.";
  }
}
