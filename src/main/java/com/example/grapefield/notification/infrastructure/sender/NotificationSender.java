package com.example.grapefield.notification.infrastructure.sender;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.notification.model.entity.ScheduleType;
import com.example.grapefield.notification.model.response.NotificationResp;
import com.example.grapefield.notification.reposistory.ScheduleNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSender {
  private final ScheduleNotificationRepository notificationRepository;
  private final SimpMessagingTemplate simpMessagingTemplate;

  // 실제 알림 발송 로직
  public void sendNotification(ScheduleNotification notification) {
    // 개인 일정 알림인 경우 isNotify 확인
    if (notification.getScheduleType() == ScheduleType.PERSONAL_SCHEDULE) {
      PersonalSchedule schedule = notification.getPersonalSchedule();
      if (schedule == null || !Boolean.TRUE.equals(schedule.getIsNotify())) {
        // isNotify가 false면 알림을 발송하지 않음
        return;
      }
    }

    // 이벤트 관심 알림인 경우 isNotify 확인 (필요한 경우)
    if (notification.getScheduleType() == ScheduleType.EVENTS_INTEREST) {
      EventsInterest interest = notification.getEventsInterest();
      if (interest == null || !Boolean.TRUE.equals(interest.getIsNotify())) {
        // isNotify가 false면 알림을 발송하지 않음
        return;
      }
    }

    // 엔티티를 응답용 DTO로 변환
    NotificationResp notificationResp = NotificationResp.fromEntity(notification);

    // WebSocket으로 알림 발송
    simpMessagingTemplate.convertAndSendToUser(
            notification.getUser().getUsername(),
            "/queue/notifications",
            notificationResp
    );
  }
}