package com.example.grapefield.events;

import com.example.grapefield.common.PageResponse;
import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.entity.EventsImg;
import com.example.grapefield.events.model.entity.TicketVendor;
import com.example.grapefield.events.model.request.EventsRegisterReq;
import com.example.grapefield.events.model.response.*;
import com.example.grapefield.events.post.BoardRepository;
import com.example.grapefield.events.post.model.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventsService {
  private final EventsRepository eventsRepository;
  private final BoardRepository boardRepository;
  private final EventsImgRepository eventsImgRepository;

  public Long eventsRegister(EventsRegisterReq request) {
    Events events = eventsRepository.save(request.toEntity());
    //events의 idx를 받아서 board 추가
    Board board = Board.builder().events(events).title(events.getTitle()).build();
    boardRepository.save(board);
  //TODO : 채팅방 추가
    return events.getIdx();
  }

  public Page<Events> getEventListWithPagination(Pageable pageable) {
    return eventsRepository.findAll(pageable);
  }

  public Map<String, List<EventsCalendarListResp>> getCalendarEvents(
      LocalDateTime date) {

    LocalDateTime startOfMonth = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endOfMonth = date.withDayOfMonth(date.toLocalDate().lengthOfMonth())
        .withHour(23).withMinute(59).withSecond(59);

    // 시작일, 종료일 기준으로 각각 조회
    List<EventsCalendarListResp> startEvents = eventsRepository.findEventsBySaleStartBetween(
        startOfMonth, endOfMonth);

    List<EventsCalendarListResp> endEvents = eventsRepository.findEventsBySaleEndBetween(
        startOfMonth, endOfMonth);

    // 결과 맵 구성
    Map<String, List<EventsCalendarListResp>> result = new HashMap<>();
    result.put("startEvents", startEvents);
    result.put("endEvents", endEvents);

    return result;
  }

  public Map<String, Slice<EventsListResp>> getMainEvents(String category) {
    Pageable pageable = PageRequest.of(0, 10);
    LocalDateTime now = LocalDateTime.now();

    EventCategory eventCategory = EventCategory.valueOf(category.toUpperCase());

    Slice<EventsListResp> recommended = eventsRepository.findTopRecommended(eventCategory, now, pageable);
    Slice<EventsListResp> popular = eventsRepository.findTopPopular(eventCategory, pageable);
    Slice<EventsListResp> upcoming = eventsRepository.findTopUpcoming(eventCategory, now, pageable);

    Map<String, Slice<EventsListResp>> result = new HashMap<>();
    result.put("recommend", recommended);
    result.put("popular", popular);
    result.put("new", upcoming);
    return result;
  }

  public Slice<EventsListResp> getMoreEventList(String category, String array, Pageable pageable) {
    LocalDateTime now = LocalDateTime.now();
    EventCategory eventCategory = EventCategory.valueOf(category.toUpperCase());
      return switch (array) {
          case "recommend" -> eventsRepository.findTopRecommended(eventCategory, now, pageable);
          case "popular" -> eventsRepository.findTopPopular(eventCategory, pageable);
          case "new" -> eventsRepository.findTopUpcoming(eventCategory, now, pageable);
          default -> null;
      };
  }

  public Map<String, Slice<EventsTicketScheduleListResp>> getMainEventsTicketSchedule() {
    Pageable pageable = PageRequest.of(0, 6);
    LocalDateTime now = LocalDateTime.now();
    Slice<EventsTicketScheduleListResp> openings = eventsRepository.findEventsWithUpcomingTicketOpenings(now, pageable);
    Slice<EventsTicketScheduleListResp> closures = eventsRepository.findEventsWithUpcomingTicketClosures(now, pageable);

    Map<String, Slice<EventsTicketScheduleListResp>> result = new HashMap<>();
    result.put("openings", openings);
    result.put("closures", closures);
    return result;
  }

  public Slice<EventsTicketScheduleListResp> getMoreEventsTicketSchedule(String type, Pageable pageable) {
    LocalDateTime now = LocalDateTime.now();
    return switch (type){
      case "openings" -> eventsRepository.findEventsWithUpcomingTicketOpenings(now, pageable);
      case "closures" -> eventsRepository.findEventsWithUpcomingTicketClosures(now, pageable);
      default -> null;
    };
  }

  public EventsDetailResp getEventDetail(Long idx) {
    return eventsRepository.getEventDetail(idx);
  }

  public List<EventsImgDetailResp> getEventDetailImages(Long idx) {
    List<EventsImg> images = eventsImgRepository.findByEventsIdxOrderByDisplayOrderAsc(idx);

    return images.stream()
        .map(image -> new EventsImgDetailResp(
            image.getImgUrl().replace("\\", "/"), // \\로 db에 저장될 경우 /로 교체
            image.getDisplayOrder()
        ))
        .collect(Collectors.toList());
  }

//  public Map<String, List<EventsCalendarListResp>> getFilteredCalendarEvents(
//      LocalDateTime date, EventCategory category, Boolean isPresale, TicketVendor ticketVendor) {
//
//    LocalDateTime startOfMonth = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
//    LocalDateTime endOfMonth = date.withDayOfMonth(date.toLocalDate().lengthOfMonth())
//        .withHour(23).withMinute(59).withSecond(59);
//
//    // 시작일, 종료일 기준으로 각각 조회
//    List<EventsCalendarListResp> startEvents = eventsRepository.findEventsBySaleStartBetween(
//        startOfMonth, endOfMonth, category, isPresale, ticketVendor);
//
//    List<EventsCalendarListResp> endEvents = eventsRepository.findEventsBySaleEndBetween(
//        startOfMonth, endOfMonth, category, isPresale, ticketVendor);
//
//    // 결과 맵 구성
//    Map<String, List<EventsCalendarListResp>> result = new HashMap<>();
//    result.put("startEvents", startEvents);
//    result.put("endEvents", endEvents);
//
//    return result;
//  }

}
