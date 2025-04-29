package com.example.grapefield.notification.model.response;

import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.notification.model.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventsInterestResp {
  private Long id;              // 즐겨찾기/관심 ID
  private Long userId;          // 사용자 ID
  private Long eventId;         // 이벤트 ID
  private Boolean isFavorite;   // 즐겨찾기 여부
  private Boolean isNotify;     // 알림 여부
  private NotificationType notificationType;  // 알림 유형
  private LocalDateTime createdAt;  // 생성 시간
  private LocalDateTime updatedAt;  // 수정 시간

  // 추가 이벤트 정보
  private EventSummaryResp event;
  private String timeUntilStart;  // 시작까지 남은 시간 (예: "내일", "3일 후")

  // 엔티티에서 DTO로 변환하는 정적 메서드
  public static EventsInterestResp fromEntity(EventsInterest interest) {
    EventsInterestResp dto = EventsInterestResp.builder()
        .id(interest.getIdx())
        .userId(interest.getUser().getIdx())
        .eventId(interest.getEvents().getIdx())
        .isFavorite(interest.getIsFavorite())
        .isNotify(interest.getIsNotify())
        .notificationType(interest.getNotificationType())
        .createdAt(interest.getCreatedAt())
        .updatedAt(interest.getUpdatedAt())
        .build();

    // 이벤트 정보 추가
    if (interest.getEvents() != null) {
      dto.setEvent(EventSummaryResp.fromEntity(interest.getEvents()));

      // 이벤트 시작까지 남은 시간 계산
      if (interest.getEvents().getStartDate() != null) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = interest.getEvents().getStartDate();

        if (startDate.isAfter(now)) {
          long days = ChronoUnit.DAYS.between(now.toLocalDate(), startDate.toLocalDate());
          if (days == 0) {
            dto.setTimeUntilStart("오늘");
          } else if (days == 1) {
            dto.setTimeUntilStart("내일");
          } else {
            dto.setTimeUntilStart(days + "일 후");
          }
        } else {
          // 이미 시작한 경우
          if (interest.getEvents().getEndDate() != null &&
              interest.getEvents().getEndDate().isAfter(now)) {
            dto.setTimeUntilStart("진행 중");
          } else {
            dto.setTimeUntilStart("종료됨");
          }
        }
      }
    }

    return dto;
  }
}
