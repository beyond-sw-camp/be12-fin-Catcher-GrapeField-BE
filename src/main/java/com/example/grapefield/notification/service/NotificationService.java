package com.example.grapefield.notification.service;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.notification.infrastructure.scheduler.NotificationScheduler;
import com.example.grapefield.notification.model.entity.*;
import com.example.grapefield.notification.model.response.NotificationResp;
import com.example.grapefield.notification.reposistory.EventsInterestRepository;
import com.example.grapefield.notification.reposistory.ScheduleNotificationRepository;
import com.example.grapefield.user.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final ScheduleNotificationRepository scheduleNotificationRepository;
  private final NotificationScheduler notificationScheduler;
  private final EventsRepository eventsRepository;
  private final EventsInterestRepository eventsInterestRepository;

  /**
   * 공연/전시 알림 토글
   */
  @Transactional
  public Boolean toggleNotify(Long eventsIdx, User user) {
    Events event = eventsRepository.findById(eventsIdx)
        .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));

    EventsInterest interest = eventsInterestRepository.findByUserAndEvents(user, event)
        .orElse(EventsInterest.builder()
            .user(user)
            .events(event)
            .isFavorite(false)
            .isNotify(false)
            .build());

    boolean newNotifyState = !Boolean.TRUE.equals(interest.getIsNotify());
    interest.setIsNotify(newNotifyState); // 자동으로 isCalendar도 갱신됨

    // 새로 만든 객체면 먼저 저장 (영속화 필요)
    if (interest.getIdx() == null) {
      eventsInterestRepository.save(interest);
    }

    if (newNotifyState) {
      boolean alreadyExists = scheduleNotificationRepository.existsByUserAndEventsInterest(user, interest);

      if (!alreadyExists) {
        // 세 가지 알림 유형 모두 생성

        // 1. 시작 10분 전 알림
        ScheduleNotification noti10min = ScheduleNotification.builder()
                .user(user)
                .eventsInterest(interest)
                .notificationTime(calculateNotificationTime(event.getStartDate(), NotificationType.BEFORE_10MIN))
                .notificationType(NotificationType.BEFORE_10MIN)
                .scheduleType(ScheduleType.EVENTS_INTEREST)
                .isRead(false)
                .build();
        scheduleNotificationRepository.save(noti10min);

        // 2. 1시간 전 알림
        ScheduleNotification noti1hour = ScheduleNotification.builder()
                .user(user)
                .eventsInterest(interest)
                .notificationTime(calculateNotificationTime(event.getStartDate(), NotificationType.BEFORE_1HOUR))
                .notificationType(NotificationType.BEFORE_1HOUR)
                .scheduleType(ScheduleType.EVENTS_INTEREST)
                .isRead(false)
                .build();
        scheduleNotificationRepository.save(noti1hour);

        // 3. 당일 오전 9시 알림
        ScheduleNotification noti9am = ScheduleNotification.builder()
                .user(user)
                .eventsInterest(interest)
                .notificationTime(calculateNotificationTime(event.getStartDate(), NotificationType.DAY_9AM))
                .notificationType(NotificationType.DAY_9AM)
                .scheduleType(ScheduleType.EVENTS_INTEREST)
                .isRead(false)
                .build();
        scheduleNotificationRepository.save(noti9am);
      }
    } else {
      // 알림 해제 시 알림 삭제
      scheduleNotificationRepository.deleteByUserAndEventsInterest(user, interest);
    }

    // 마지막으로 interest 저장 (토글이 기존 객체였던 경우)
    eventsInterestRepository.save(interest);

    return true;
  }

  /**
   * 개인 일정 알림 생성
   */
  public void createPersonalScheduleNotification(PersonalSchedule personalSchedule) {
    if (personalSchedule.getIsNotify()) {
      User user = personalSchedule.getUser();

      // 1. 시작 10분 전 알림
      ScheduleNotification notification10min = ScheduleNotification.builder()
              .user(user)
              .personalSchedule(personalSchedule)
              .isRead(false)
              .notificationTime(calculateNotificationTime(personalSchedule.getStartDate(), NotificationType.BEFORE_10MIN))
              .notificationType(NotificationType.BEFORE_10MIN)
              .scheduleType(ScheduleType.PERSONAL_SCHEDULE)
              .build();
      scheduleNotificationRepository.save(notification10min);
      notificationScheduler.scheduleNotification(notification10min);

      // 2. 1시간 전 알림
      ScheduleNotification notification1hour = ScheduleNotification.builder()
              .user(user)
              .personalSchedule(personalSchedule)
              .isRead(false)
              .notificationTime(calculateNotificationTime(personalSchedule.getStartDate(), NotificationType.BEFORE_1HOUR))
              .notificationType(NotificationType.BEFORE_1HOUR)
              .scheduleType(ScheduleType.PERSONAL_SCHEDULE)
              .build();
      scheduleNotificationRepository.save(notification1hour);
      notificationScheduler.scheduleNotification(notification1hour);

      // 3. 당일 오전 9시 알림
      ScheduleNotification notification9am = ScheduleNotification.builder()
              .user(user)
              .personalSchedule(personalSchedule)
              .isRead(false)
              .notificationTime(calculateNotificationTime(personalSchedule.getStartDate(), NotificationType.DAY_9AM))
              .notificationType(NotificationType.DAY_9AM)
              .scheduleType(ScheduleType.PERSONAL_SCHEDULE)
              .build();
      scheduleNotificationRepository.save(notification9am);
      notificationScheduler.scheduleNotification(notification9am);
    }
  }

  /**
   * 알림 시간 계산 유틸
   */
  private LocalDateTime calculateNotificationTime(LocalDateTime startDate, NotificationType type) {
    switch (type) {
      case BEFORE_10MIN:
        return startDate.minusMinutes(10);
      case BEFORE_1HOUR:
        return startDate.minusHours(1);
      case DAY_9AM:
        // 당일 오전 9시로 설정
        return LocalDateTime.of(
                startDate.toLocalDate(), // 시작 날짜의 날짜 부분만 가져옵니다
                LocalTime.of(9, 0) // 오전 9시로 설정
        );
      case CUSTOM_MESSAGE:
        // 기타 운영자 알림은 별도 처리 필요
        return startDate; // 기본값 또는 필요에 따라 조정
      default:
        return startDate;
    }
  }

  /**
   * 알림 읽음 처리
   */
  public void markAsRead(Long notificationId) {
    scheduleNotificationRepository.findById(notificationId).ifPresent(notification -> {
      notification.markAsRead();
      scheduleNotificationRepository.save(notification);
    });
  }

  /**
   * 사용자의 모든 알림 조회
   */
  public List<NotificationResp> getUserNotifications(Long userId) {
    return scheduleNotificationRepository.findNotificationsByUserIdx(userId);
  }

  /**
   * 사용자의 읽지 않은 알림만 조회
   */
  public List<NotificationResp> getUnreadNotifications(Long userId) {
    return scheduleNotificationRepository.findUnreadNotificationsByUserIdx(userId);
  }

  /**
   * 사용자의 모든 알림을 읽음 처리
   */
  @Transactional
  public void markAllAsRead(Long userId) {
    scheduleNotificationRepository.markAllAsReadByUserId(userId);
  }

  /**
   * 알림 숨김 처리 (UI에서 제거)
   */
  @Transactional
  public void hideNotification(Long notificationId) {
    scheduleNotificationRepository.findById(notificationId).ifPresent(notification -> {
      notification.hide();
      scheduleNotificationRepository.save(notification);
    });
  }
}
