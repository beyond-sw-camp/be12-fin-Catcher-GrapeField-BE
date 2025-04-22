package com.example.grapefield.notification.infrastructure.scheduler;

import com.example.grapefield.notification.infrastructure.sender.NotificationSender;
import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.notification.reposistory.ScheduleNotificationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
  private final TaskScheduler taskScheduler;
  private final NotificationSender notificationSender; // 생성자 주입으로 변경
  private final ScheduleNotificationRepository notificationRepository; // 추가: 알림 조회를 위한 리포지토리
  private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

  private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

  // 애플리케이션 시작 시 실행되는 메서드
  @PostConstruct
  public void init() {
    scheduleAllFutureNotifications();
  }

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
    Date notificationDate = Date.from(notification.getNotificationTime().atZone(ZoneId.systemDefault()).toInstant());

    // 현재 시간보다 미래인 경우에만 스케줄링
    if (notificationDate.after(new Date())) {
      NotificationTask task = new NotificationTask(notification, notificationSender);
      ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, notificationDate);
      scheduledTasks.put(notification.getIdx(), scheduledTask);
      log.debug("알림 ID: {}, 시간: {}에 스케줄링 되었습니다.", notification.getIdx(), notification.getNotificationTime());
    } else {
      // 이미 지난 시간의 알림은 즉시 처리
      log.debug("알림 ID: {}의 시간({})이 이미 지났습니다. 즉시 발송합니다.", notification.getIdx(), notification.getNotificationTime());
      notificationSender.sendNotification(notification);
    }
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