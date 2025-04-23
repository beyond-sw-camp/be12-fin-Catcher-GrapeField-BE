package com.example.grapefield.notification.infrastructure.scheduler;

import com.example.grapefield.notification.infrastructure.sender.NotificationSender;
import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.notification.model.entity.ScheduleType;
import com.example.grapefield.notification.reposistory.EventsInterestRepository;
import com.example.grapefield.notification.reposistory.PersonalScheduleRepository;
import com.example.grapefield.notification.reposistory.ScheduleNotificationRepository;
import com.example.grapefield.notification.service.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
  private final TaskScheduler taskScheduler;
  private final NotificationSender notificationSender; // 생성자 주입으로 변경
  private final ScheduleNotificationRepository notificationRepository; // 추가: 알림 조회를 위한 리포지토리
  private final PersonalScheduleRepository personalScheduleRepository;
  private final EventsInterestRepository eventsInterestRepository;

  private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

  private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

  // 미래의 모든 알림 스케줄링 (애플리케이션 재시작 시 필요)
  private void scheduleAllFutureNotifications() {
    // 현재 시간 이후에 예정된 모든 알림 조회
    LocalDateTime now = LocalDateTime.now();
    List<ScheduleNotification> futureNotifications =
        notificationRepository.findByNotificationTimeAfterAndIsReadFalse(now);

    // 각 알림 스케줄링
    for (ScheduleNotification notification : futureNotifications) {
      scheduleNotification(notification);
    }

    log.info("총 {}개의 알림이 로드되어 스케줄링 되었습니다.", futureNotifications.size());
  }

  // 새 알림 스케줄링
  public void scheduleNotification(ScheduleNotification notification) {
    // 현재 시간 기준으로 알림 날짜 변환
    Date notificationDate = Date.from(notification.getNotificationTime().atZone(ZoneId.systemDefault()).toInstant());
    Date currentDate = new Date();

    // 1. 초기 알림 상태 확인
    boolean shouldSchedule = checkNotifyStatus(notification);

    // 스케줄링 조건을 만족하지 않으면 로깅 후 종료
    if (!shouldSchedule) {
      log.debug("알림 ID: {}의 isNotify 상태가 false여서 스케줄링하지 않습니다.", notification.getIdx());
      return;
    }

    // 2. 시간에 따른 처리 분기
    if (notificationDate.after(currentDate)) {
      // 미래 시간인 경우 스케줄링
      scheduleNotificationTask(notification, notificationDate);
    } else {
      // 지난 시간인 경우 즉시 처리 (단, 재확인 후)
      log.debug("알림 ID: {}의 시간({})이 이미 지났습니다. isNotify 확인 후 즉시 발송합니다.",
          notification.getIdx(), notification.getNotificationTime());

      // 발송 전 다시 상태 확인
      if (checkNotifyStatus(notification)) {
        notificationSender.sendNotification(notification);
      } else {
        log.debug("알림 ID: {}의 isNotify 상태가 false여서 발송하지 않습니다.", notification.getIdx());
      }
    }
  }

  // 알림 스케줄링 작업 생성 메서드
  private void scheduleNotificationTask(ScheduleNotification notification, Date notificationDate) {
    NotificationTask task = new NotificationTask(notification, notificationSender) {
      @Override
      public void run() {
        // 발송 직전에 다시 isNotify 상태 확인
        if (checkNotifyStatus(notification)) {
          super.run(); // 알림 발송
        } else {
          log.debug("알림 ID: {}의 isNotify 상태가 false로 변경되어 발송하지 않습니다.", notification.getIdx());
        }
      }
    };

    ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, notificationDate);
    scheduledTasks.put(notification.getIdx(), scheduledTask);
    log.debug("알림 ID: {}, 시간: {}에 스케줄링 되었습니다.", notification.getIdx(), notification.getNotificationTime());
  }

  // 알림 상태 확인 메서드
  private boolean checkNotifyStatus(ScheduleNotification notification) {
    if (notification.getScheduleType() == ScheduleType.PERSONAL_SCHEDULE
        && notification.getPersonalSchedule() != null) {
      PersonalSchedule schedule = personalScheduleRepository
          .findById(notification.getPersonalSchedule().getIdx())
          .orElse(null);
      return schedule != null && Boolean.TRUE.equals(schedule.getIsNotify());
    } else if (notification.getScheduleType() == ScheduleType.EVENTS_INTEREST
        && notification.getEventsInterest() != null) {
      EventsInterest interest = eventsInterestRepository
          .findById(notification.getEventsInterest().getIdx())
          .orElse(null);
      return interest != null && Boolean.TRUE.equals(interest.getIsNotify());
    }
    return false;
  }

  // 알림 취소
  public void cancelNotification(Long notificationId) {
    ScheduledFuture<?> scheduledTask = scheduledTasks.get(notificationId);
    if (scheduledTask != null) {
      boolean canceled = scheduledTask.cancel(false);
      scheduledTasks.remove(notificationId);
      log.debug("알림 ID: {}의 스케줄이 취소되었습니다. 취소 결과: {}", notificationId, canceled);
    }
  }

  // 알림 스케줄 업데이트
  public void updateNotification(ScheduleNotification notification) {
    // 기존 스케줄 취소
    cancelNotification(notification.getIdx());
    // 새로운 시간으로 재스케줄링
    scheduleNotification(notification);
  }

  // 유형별 알림 취소 메서드 추가
  public void cancelEventNotifications(Long eventsInterestId) {
    // 리포지토리에서 해당 이벤트 관심 ID에 대한 모든 알림 조회
    List<ScheduleNotification> notifications =
        notificationRepository.findByEventsInterestIdx(eventsInterestId);

    // 각 알림 취소
    for (ScheduleNotification notification : notifications) {
      cancelNotification(notification.getIdx());
    }
  }
}