package com.example.grapefield.notification.reposistory;

import com.example.grapefield.notification.model.response.EventInterestCalendarResp;

import java.time.LocalDateTime;
import java.util.List;

public interface EventsInterestCustomRepository {
  List<EventInterestCalendarResp> findMyInterestedEventsBetween(Long userIdx, LocalDateTime start, LocalDateTime end);
}
