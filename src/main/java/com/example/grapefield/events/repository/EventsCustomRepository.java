package com.example.grapefield.events.repository;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.entity.TicketVendor;
import com.example.grapefield.events.model.response.*;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.example.grapefield.events.model.response.EventsCalendarListResp;
import com.example.grapefield.events.model.response.EventsDetailResp;
import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.model.response.EventsTicketScheduleListResp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventsCustomRepository {
  // 시작일 기준 이벤트 조회
  List<EventsCalendarListResp> findEventsBySaleStartBetween(
      LocalDateTime startDate, LocalDateTime endDate);

  // 시작일 기준 이벤트 상세 조회
  List<EventsDetailCalendarListResp> findDetailEventsBySaleStartBetween(
          LocalDateTime startDate, LocalDateTime endDate);

  // 종료일 기준 이벤트 조회
  List<EventsCalendarListResp> findEventsBySaleEndBetween(
      LocalDateTime startDate, LocalDateTime endDate);

  //추천 (진행 중 or 예정 중 즐찾 많은 순)
  Slice<EventsListResp> findTopRecommended(EventCategory category, LocalDateTime now, Pageable pageable);
  //인기 (진행 여부 상관없이 즐찾 많은 순)
  Slice<EventsListResp> findTopPopular(EventCategory category, Pageable pageable);
  //신규 (startDate > now)
  Slice<EventsListResp> findTopUpcoming(EventCategory category, LocalDateTime now, Pageable pageable);

  //(티켓팅) 오픈 예정 공연
  Slice<EventsTicketScheduleListResp> findEventsWithUpcomingTicketOpenings(LocalDateTime now, Pageable pageable);
  //(티켓팅) 종료 예정 공연
  Slice<EventsTicketScheduleListResp> findEventsWithUpcomingTicketClosures(LocalDateTime now, Pageable pageable);

  //공연/전시 상세 페이지 정보
  EventsDetailResp getEventDetail(Long eventsIdx);

  Map<String, Object> getParticipantDetail(Long eventsIdx);

  Page<EventsListResp> findEventsByKeyword(String keyword, Pageable pageable, User user);
  Page<EventsListResp> findEventsByKeywordAnd(List<String> keywords, Pageable pageable, User user);
}