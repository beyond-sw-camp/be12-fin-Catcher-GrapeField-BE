package com.example.grapefield.notification.service;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.notification.model.request.EventsInterestReq;
import com.example.grapefield.notification.model.response.EventsInterestResp;
import com.example.grapefield.notification.reposistory.EventsInterestRepository;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventsInterestService {
  private final EventsInterestRepository eventsInterestRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;
  private final EventsRepository eventsRepository;

  @Transactional
  public EventsInterestResp registerEventInterest(EventsInterestReq dto) {
    // User와 Events 엔티티 조회
    User user = userRepository.findById(dto.getUserIdx())
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + dto.getUserIdx()));

    Events event = eventsRepository.findById(dto.getEventIdx())
        .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다: " + dto.getEventIdx()));

    // 이미 같은 사용자-이벤트 조합의 관심 정보가 있는지 확인
    Optional<EventsInterest> existingInterest = eventsInterestRepository.findByUserAndEvents(user, event);

    EventsInterest eventsInterest;

    if (existingInterest.isPresent()) {
      // 이미 존재하면 업데이트
      eventsInterest = existingInterest.get();
      eventsInterest.setIsFavorite(dto.getIsFavorite());
      eventsInterest.setIsNotify(dto.getIsNotify());
      eventsInterest.setNotificationType(dto.getNotificationType());
    } else {
      // 새로 생성
      eventsInterest = EventsInterest.builder()
          .user(user)
          .events(event)
          .isFavorite(dto.getIsFavorite())
          .isNotify(dto.getIsNotify())
          .notificationType(dto.getNotificationType())
          .build();
    }

    // 저장
    eventsInterest = eventsInterestRepository.save(eventsInterest);

    // 알림 설정이 있으면 알림 생성
    if (eventsInterest.getIsNotify()) {
      notificationService.createEventNotification(eventsInterest);
    }

    // 엔티티를 응답 DTO로 변환하여 반환
    return EventsInterestResp.fromEntity(eventsInterest);
  }

//  @Transactional
//  public EventsInterestResp updateEventInterest(Long id, EventsInterestReq dto) {
//    // 이벤트 관심 정보 업데이트 로직
//    EventsInterest eventsInterest = /* 업데이트 로직 */;
//
//    // 알림 설정이 변경되었을 경우 알림 업데이트
//    notificationService.createEventNotification(eventsInterest); // 기존 알림은 자동으로 대체됨
//
//    // 엔티티를 응답 DTO로 변환하여 반환
//    return EventsInterestResp.fromEntity(eventsInterest);
//  }
}