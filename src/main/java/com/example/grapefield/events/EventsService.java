package com.example.grapefield.events;

import com.example.grapefield.common.PageResponse;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.request.EventsRegisterReq;
import com.example.grapefield.events.model.response.EventsCalendarListResp;
import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.BoardRepository;
import com.example.grapefield.events.post.model.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventsService {
  private final EventsRepository eventsRepository;
  private final BoardRepository boardRepository;

  public Long eventsRegister(EventsRegisterReq request) {
    Events events = eventsRepository.save(request.toEntity());
    //events의 idx를 받아서 board 추가
    Board board = Board.builder().events(events).title(events.getTitle()).build();
    boardRepository.save(board);
  //TODO : 채팅방 추가
    return events.getIdx();
  }

  public PageResponse<EventsListResp> getEventListWithPagination(Pageable pageable) {
    Page<Events> eventPage = eventsRepository.findAll(pageable);
    Page<EventsListResp> eventDtoPage = eventPage.map(event -> EventsListResp.builder()
            .idx(event.getIdx())
            .title(event.getTitle())
            .category(event.getCategory())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .posterImgUrl(event.getPosterImgUrl())
            .venue(event.getVenue())
            .build());
    return PageResponse.from(eventDtoPage, eventDtoPage.getContent());
  }

  public List<EventsCalendarListResp> getCalendarEvents(LocalDateTime date) {
    List<Events> events = eventsRepository.getByDate();
    List<EventsCalendarListResp> eventList = new ArrayList<>();
  }
}
