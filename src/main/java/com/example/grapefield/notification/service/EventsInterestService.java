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
  public Boolean toggleEventInterest(Long idx, User user) {
    Events event = eventsRepository.findById(idx)
        .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다"));

    EventsInterest interest = eventsInterestRepository.findByUserAndEvents(user, event)
        .orElse(EventsInterest.builder()
            .user(user)
            .events(event)
            .isFavorite(false)
            .isNotify(false)
            .isCalendar(false)
            .build());

    // 즐겨찾기 상태 토글
    interest.setIsFavorite(!Boolean.TRUE.equals(interest.getIsFavorite()));
    eventsInterestRepository.save(interest);

    return true;
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