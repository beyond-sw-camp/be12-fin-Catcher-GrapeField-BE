package com.example.grapefield.events;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.entity.TicketVendor;
import com.example.grapefield.events.model.response.EventsCalendarListResp;
import com.example.grapefield.events.model.response.EventsListResp;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface EventsCustomRepository {
  // 시작일 기준 이벤트 조회
  List<EventsCalendarListResp> findEventsBySaleStartBetween(
      LocalDateTime startDate, LocalDateTime endDate);

  // 종료일 기준 이벤트 조회
  List<EventsCalendarListResp> findEventsBySaleEndBetween(
      LocalDateTime startDate, LocalDateTime endDate);

  // 전체 조회
  Slice<EventsListResp> findAllOrdered(Pageable pageable);
  // 특정 카테고리 필터링
  Slice<EventsListResp> findAllFilteredByCategory(EventCategory category, Pageable pageable);

  //추천 (진행 중 or 예정 중 즐찾 많은 순)
  Slice<EventsListResp> findTopRecommended(EventCategory category, LocalDateTime now, Pageable pageable);
  //인기 (진행 여부 상관없이 즐찾 많은 순)
  Slice<EventsListResp> findTopPopular(EventCategory category, Pageable pageable);
  //신규 (startDate > now)
  Slice<EventsListResp> findTopUpcoming(EventCategory category, LocalDateTime now, Pageable pageable);
}