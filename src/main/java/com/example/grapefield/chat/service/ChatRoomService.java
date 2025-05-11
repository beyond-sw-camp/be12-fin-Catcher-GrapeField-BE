package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageCurrentRepository chatMessageCurrentRepository;
    public ChatRoom findByIdx(Long roomIdx) {
        return chatRoomRepository.findById(roomIdx)
                .orElseThrow(()->
                        new NoSuchElementException("해당 채팅방이 존재하지 않습니다. roomIdx: " + roomIdx));
    }

    /**
     * roomIdx 방의 메시지를 페이징하여 조회합니다.
     * @param roomIdx 조회할 채팅방 ID
     * @param page    0부터 시작하는 페이지 번호
     * @param size    한 페이지당 메시지 수
     */
    public Page<ChatMessageCurrent> getPaginatedMessages(Long roomIdx, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return chatMessageCurrentRepository.findByChatRoom_IdxOrderByCreatedAtDesc(roomIdx, pageable);
    }


    @Transactional
    public void increaseHeartCount(Long roomIdx) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        chatRoom.increaseHeart(); // heartCnt += 1
    }

}
