package com.example.grapefield.common;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.post.repository.BoardRepository;
import com.example.grapefield.events.post.model.entity.Board;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class EventResourceInitializer implements ApplicationRunner {

  private final EventsRepository eventsRepository;
  private final BoardRepository boardRepository;
  private final ChatRoomRepository chatRoomRepository;

  @Override
  public void run(ApplicationArguments args) {
    List<Events> allEvents = eventsRepository.findAll();

    for (Events events : allEvents) {
      // 게시판 초기화
      if (!boardRepository.existsById(events.getIdx())) {
        Board board = Board.builder()
            .events(events)
            .title(events.getTitle())
            .build();
        boardRepository.save(board);
      }

      // 채팅방 초기화
      if (!chatRoomRepository.existsById(events.getIdx())) {
        ChatRoom chatRoom = ChatRoom.builder()
            .events(events)
            .roomName(events.getTitle() + " 채팅방")
            .createdAt(LocalDateTime.now())
            .heartCnt(0L)
            .build();
        chatRoomRepository.save(chatRoom);
      }
    }
  }
}