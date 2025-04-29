package com.example.grapefield.notification.model.request;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.notification.model.entity.NotificationType;
import com.example.grapefield.user.model.entity.User;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventsInterestReq {
  private Long userIdx;          // 사용자 ID
  private Long eventIdx;         // 이벤트 ID
  private Boolean isFavorite;   // 즐겨찾기 여부
  private Boolean isNotify;     // 알림 여부
  private NotificationType notificationType;  // 알림 유형

  // DTO를 엔티티로 변환
  public EventsInterest toEntity(User user, Events event) {
    return EventsInterest.builder()
        .user(user)
        .events(event)
        .isFavorite(this.isFavorite)
        .isNotify(this.isNotify)
        .notificationType(this.notificationType)
        .build();
  }

  // 기존 엔티티를 업데이트
  public void updateEntity(EventsInterest interest) {
    if (this.isFavorite != null) {
      interest.setIsFavorite(this.isFavorite);
    }
    if (this.isNotify != null) {
      interest.setIsNotify(this.isNotify);
    }
    if (this.notificationType != null) {
      interest.setNotificationType(this.notificationType);
    }
  }
}
