package com.example.demo.events;

import com.example.demo.events.model.entity.Events;
import com.example.demo.events.model.request.EventsRegisterReq;
import com.example.demo.events.post.BoardRepository;
import com.example.demo.events.post.model.entity.Board;
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
