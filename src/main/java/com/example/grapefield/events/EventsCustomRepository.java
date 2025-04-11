package com.example.grapefield.events;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.entity.TicketVendor;
import com.example.grapefield.events.model.response.EventsCalendarListResp;

import java.time.LocalDateTime;
import java.util.List;

public interface EventsCustomRepository {
  // 시작일 기준 이벤트 조회
  List<EventsCalendarListResp> findEventsBySaleStartBetween(
      LocalDateTime startDate, LocalDateTime endDate);

  // 종료일 기준 이벤트 조회
  List<EventsCalendarListResp> findEventsBySaleEndBetween(
      LocalDateTime startDate, LocalDateTime endDate);

}