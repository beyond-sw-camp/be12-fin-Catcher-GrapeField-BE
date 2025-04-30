package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.ChatroomMember;
import com.example.grapefield.chat.model.response.ChatListResp;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomMemberRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.events.model.entity.Events;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom findByIdx(Long roomIdx) {
        return chatRoomRepository.findById(roomIdx)
                .orElseThrow(()->
                        new NoSuchElementException("해당 채팅방이 존재하지 않습니다. roomIdx: " + roomIdx));
    }


    @Transactional
    public void increaseHeartCount(Long roomIdx) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        chatRoom.increaseHeart(); // heartCnt += 1
    }

}
