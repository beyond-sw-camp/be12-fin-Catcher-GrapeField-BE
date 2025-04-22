package com.example.grapefield.notification.service;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.notification.infrastructure.scheduler.NotificationScheduler;
import com.example.grapefield.notification.model.entity.*;
import com.example.grapefield.notification.reposistory.ScheduleNotificationRepository;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final ScheduleNotificationRepository scheduleNotificationRepository;
  private final NotificationScheduler notificationScheduler;

  // 공연/전시 알림 생성
  public void createEventNotification(EventsInterest eventsInterest) {
    if (eventsInterest.getIsNotify()) {
      Events event = eventsInterest.getEvents();
      NotificationType type = eventsInterest.getNotificationType();
      User user = eventsInterest.getUser();

      // 알림 시간 계산
      LocalDateTime notificationTime = calculateNotificationTime(event.getStartDate(), type);

      // 알림 엔티티 생성 및 저장
      ScheduleNotification notification = ScheduleNotification.builder()
          .user(user)
          .eventsInterest(eventsInterest)
          .isRead(false)
          .notificationTime(notificationTime)
          .notificationType(type)
          .scheduleType(ScheduleType.EVENTS_INTEREST)
          .build();

      scheduleNotificationRepository.save(notification);

      // 스케줄러에 알림 작업 등록
      notificationScheduler.scheduleNotification(notification);
    }
  }

  // 개인 일정 알림 생성
  public void createPersonalScheduleNotification(PersonalSchedule personalSchedule) {
    if (personalSchedule.getIsNotify()) {
      User user = personalSchedule.getUser();

      // 개인 일정은 항상 START_REMINDER 타입으로 가정
      NotificationType type = NotificationType.START_REMINDER;

      // 알림 시간은 일정 시작일의 자정으로 설정
      LocalDateTime notificationTime = personalSchedule.getStartDate().atStartOfDay();

      ScheduleNotification notification = ScheduleNotification.builder()
          .user(user)
          .personalSchedule(personalSchedule)
          .isRead(false)
          .notificationTime(notificationTime)
          .notificationType(type)
          .scheduleType(ScheduleType.PERSONAL_SCHEDULE)
          .build();

      scheduleNotificationRepository.save(notification);

      // 스케줄러에 알림 작업 등록
      notificationScheduler.scheduleNotification(notification);
    }
  }

  // 알림 시간 계산 로직
  private LocalDateTime calculateNotificationTime(LocalDateTime eventStart, NotificationType type) {
    switch (type) {
      case START_REMINDER:
        // 이벤트 당일 오전 9시로 설정
        return eventStart.toLocalDate().atTime(9, 0);
      case HOUR_REMINDER:
        // 이벤트 시작 1시간 전
        return eventStart.minusHours(1);
      case CUSTOM_MESSAGE:
        // 기타 메시지는 즉시 발송
        return LocalDateTime.now();
      default:
        return eventStart;
    }
  }

  // 알림 읽음 처리
  public void markAsRead(Long notificationId) {
    scheduleNotificationRepository.findById(notificationId).ifPresent(notification -> {
      notification.markAsRead();
      scheduleNotificationRepository.save(notification);
    });
  }

  // 사용자의 모든 알림 조회
  public List<ScheduleNotification> getUserNotifications(Long userId) {
    return scheduleNotificationRepository.findByUserIdxOrderByNotificationTimeDesc(userId);
  }

  // 사용자의 읽지 않은 알림 조회
  public List<ScheduleNotification> getUnreadNotifications(Long userId) {
    return scheduleNotificationRepository.findByUserIdxAndIsReadFalseOrderByNotificationTimeDesc(userId);
  }
}
