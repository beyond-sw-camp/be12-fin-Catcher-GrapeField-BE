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
    // 알림 메시지 생성
    String message = buildNotificationMessage(notification);
    // 엔티티를 응답용 DTO로 변환
    NotificationResp notificationResp = NotificationResp.fromEntity(notification);
    // 생성된 메시지를 DTO에 설정
    notificationResp.setMessage(message);

    // WebSocket으로 알림 발송
    simpMessagingTemplate.convertAndSendToUser(
        notification.getUser().getUsername(),
        "/queue/notifications",
        notificationResp
    );
  }

  // 알림 메시지 생성
  private String buildNotificationMessage(ScheduleNotification notification) {
    StringBuilder message = new StringBuilder();

    if (notification.getScheduleType() == ScheduleType.EVENTS_INTEREST) {
      EventsInterest interest = notification.getEventsInterest();
      Events event = interest.getEvents();

      switch (notification.getNotificationType()) {
        case START_REMINDER:
          message.append("[오늘] ")
              .append(event.getTitle())
              .append(" 티켓팅이 오늘 시작합니다.");
          break;
        case HOUR_REMINDER:
          message.append("[1시간 전] ")
              .append(event.getTitle())
              .append(" 티켓팅이 1시간 후에 시작합니다.");
          break;
        case CUSTOM_MESSAGE:
          message.append("[알림] ")
              .append(event.getTitle())
              .append("에 관한 소식이 있습니다.");
          break;
      }
    } else if (notification.getScheduleType() == ScheduleType.PERSONAL_SCHEDULE) {
      PersonalSchedule schedule = notification.getPersonalSchedule();

      message.append("[일정] ")
          .append(schedule.getTitle())
          .append("가 오늘 시작합니다.");
    }

    return message.toString();
  }
}