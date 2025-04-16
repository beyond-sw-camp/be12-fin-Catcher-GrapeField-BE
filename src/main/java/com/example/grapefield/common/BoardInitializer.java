package com.example.grapefield.common;

import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.post.repository.BoardRepository;
import com.example.grapefield.events.post.model.entity.Board;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class BoardInitializer implements ApplicationRunner {

  private final EventsRepository eventsRepository;
  private final BoardRepository boardRepository;

  //TODO : 채팅방도 추가 필요
  @Override
  public void run(ApplicationArguments args) {
    List<Events> allEvents = eventsRepository.findAll();

    for (Events events : allEvents) {
      if (!boardRepository.existsById(events.getIdx())) {
        Board board = Board.builder().events(events).title(events.getTitle()).build();
        boardRepository.save(board);
      }
    }
  }
}