package com.example.grapefield.events;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.request.EventsRegisterReq;
import com.example.grapefield.events.post.BoardRepository;
import com.example.grapefield.events.post.model.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsService {
  private final EventsRepository eventsRepository;
  private final BoardRepository boardRepository;

  public Long eventsRegister(EventsRegisterReq request) {
    Events events = eventsRepository.save(request.toEntity());
    Board board = Board.builder().events(events).title(events.getTitle()).build();
    boardRepository.save(board);
    return events.getIdx();
  }
}
