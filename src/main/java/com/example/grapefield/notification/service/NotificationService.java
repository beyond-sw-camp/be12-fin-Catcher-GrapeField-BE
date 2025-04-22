package com.example.grapefield.notification.service;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.notification.infrastructure.scheduler.NotificationScheduler;
import com.example.grapefield.notification.model.entity.*;
import com.example.grapefield.notification.reposistory.EventsInterestRepository;
import com.example.grapefield.notification.reposistory.ScheduleNotificationRepository;
import com.example.grapefield.user.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        ScheduleNotification noti = ScheduleNotification.builder()
            .user(user)
            .eventsInterest(interest)
            .notificationTime(calculateNotificationTime(event.getStartDate(), NotificationType.HOUR_REMINDER))
            .notificationType(NotificationType.HOUR_REMINDER)
            .scheduleType(ScheduleType.EVENTS_INTEREST)
            .isRead(false)
            .build();

        scheduleNotificationRepository.save(noti);
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

      ScheduleNotification notification = ScheduleNotification.builder()
          .user(user)
          .personalSchedule(personalSchedule)
          .isRead(false)
          .notificationTime(calculateNotificationTime(personalSchedule.getStartDate(), NotificationType.START_REMINDER))
          .notificationType(NotificationType.START_REMINDER)
          .scheduleType(ScheduleType.PERSONAL_SCHEDULE)
          .build();

      scheduleNotificationRepository.save(notification);
      notificationScheduler.scheduleNotification(notification);
    }
  }

  /**
   * 알림 시간 계산 유틸
   */
  private LocalDateTime calculateNotificationTime(LocalDateTime eventStart, NotificationType type) {
    return switch (type) {
      case START_REMINDER -> eventStart.toLocalDate().atTime(9, 0);
      case HOUR_REMINDER -> eventStart.minusHours(1);
      case CUSTOM_MESSAGE -> LocalDateTime.now();
      default -> eventStart;
    };
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
  public List<ScheduleNotification> getUserNotifications(Long userId) {
    return scheduleNotificationRepository.findByUserIdxOrderByNotificationTimeDesc(userId);
  }

  /**
   * 사용자의 읽지 않은 알림만 조회
   */
  public List<ScheduleNotification> getUnreadNotifications(Long userId) {
    return scheduleNotificationRepository.findByUserIdxAndIsReadFalseOrderByNotificationTimeDesc(userId);
  }
}
