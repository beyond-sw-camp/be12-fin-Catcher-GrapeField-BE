package com.example.grapefield.notification.model.response;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.notification.model.entity.NotificationType;
import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.notification.model.entity.ScheduleType;
import com.example.grapefield.notification.util.NotificationMessageUtil;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResp {
  private Long idx;                    // 알림 ID
  private Long userIdx;                // 사용자 ID
  private String title;                // 알림 제목
  private String message;              // 알림 메시지 (직접 생성)
  private LocalDateTime notificationTime;  // 알림 예정 시간
  private Boolean isRead;              // 읽음 여부
  private String notificationType;     // 알림 유형 (enum이 아닌 문자열로 변환)
  private String scheduleType;         // 알림 대상 유형 (enum이 아닌 문자열로 변환)

  // 최소한의 필요 정보만 포함
  private EventMinimalInfo eventInfo;        // 최소한의 이벤트 정보
  private ScheduleMinimalInfo scheduleInfo;  // 최소한의 일정 정보

  // 사용자 친화적 시간 표시
  private String formattedTime;        // "10분 전", "1시간 전" 등

  // 내부 DTO 클래스 - 이벤트 최소 정보
  @Getter @Builder
  public static class EventMinimalInfo {
    private Long idx;
    private String title;
    private String venue;
  }

  // 내부 DTO 클래스 - 일정 최소 정보
  @Getter @Builder
  public static class ScheduleMinimalInfo {
    private Long idx;
    private String title;
  }

  //QueryDSL 프로젝션을 위한 생성자
  public NotificationResp(Long idx, Long userIdx, String title, String message, LocalDateTime notificationTime, Boolean isRead, String notificationType, String scheduleType, String formattedTime) {
    this.idx = idx;
    this.userIdx = userIdx;
    this.title = title;
    this.message = message;
    this.notificationTime = notificationTime;
    this.isRead = isRead;
    this.notificationType = notificationType;
    this.scheduleType = scheduleType;
    this.formattedTime = formattedTime;
    // eventInfo와 scheduleInfo는 초기화하지 않음 (null로 유지)
  }

  // 알림 엔티티를 DTO로 변환
  public static NotificationResp fromEntity(ScheduleNotification notification) {
    if (notification == null) {
      return null;
    }

    try {
      NotificationResp dto = NotificationResp.builder()
              .idx(notification.getIdx())
              .userIdx(notification.getUser() != null ? notification.getUser().getIdx() : null)
              .notificationTime(notification.getNotificationTime())
              .isRead(notification.getIsRead())
              .notificationType(notification.getNotificationType().name())
              .scheduleType(notification.getScheduleType().name())
              .formattedTime(formatTimeAgo(notification.getNotificationTime()))
              .build();

      // 공통 유틸리티 클래스를 사용하여 메시지 생성
      dto.setMessage(NotificationMessageUtil.buildMessage(notification));

      // 이벤트 정보 처리
      if (notification.getScheduleType() == ScheduleType.EVENTS_INTEREST
              && notification.getEventsInterest() != null
              && notification.getEventsInterest().getEvents() != null) {

        Events event = notification.getEventsInterest().getEvents();

        // 제목 설정
        dto.setTitle("공연/전시 알림");

        // 최소한의 이벤트 정보만 설정
        EventMinimalInfo eventInfo = EventMinimalInfo.builder()
                .idx(event.getIdx())
                .title(event.getTitle())
                .venue(event.getVenue())
                .build();

        dto.setEventInfo(eventInfo);
      }

      // 개인 일정 정보 처리
      if (notification.getScheduleType() == ScheduleType.PERSONAL_SCHEDULE
              && notification.getPersonalSchedule() != null) {

        PersonalSchedule schedule = notification.getPersonalSchedule();

        // 제목 설정
        dto.setTitle("개인 일정 알림");

        // 최소한의 일정 정보만 설정
        ScheduleMinimalInfo scheduleInfo = ScheduleMinimalInfo.builder()
                .idx(schedule.getIdx())
                .title(schedule.getTitle())
                .build();

        dto.setScheduleInfo(scheduleInfo);
      }

      // 제목이 설정되지 않은 경우 기본값 설정
      if (dto.getTitle() == null) {
        dto.setTitle("알림");
      }

      return dto;
    } catch (Exception e) {
      // 변환 중 오류 발생 시 기본 값으로 dto 생성
      return NotificationResp.builder()
              .idx(notification.getIdx())
              .title("알림")
              .message("알림이 도착했습니다.")
              .isRead(notification.getIsRead())
              .build();
    }
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
}
