package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.response.ChatHighlightResp;
import com.example.grapefield.chat.model.response.ChatRoomItemResp;
import com.example.grapefield.chat.model.response.ChatRoomMessageResp;
import com.example.grapefield.chat.repository.ChatHighlightRepository;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.user.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatQueryService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageCurrentRepository messageRepository;
    private final ChatHighlightRepository highlightRepository;

    public ChatRoomItemResp getChatRoomItem(Long roomIdx, CustomUserDetails currentUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        List<ChatRoomMessageResp> messages = messageRepository.findAllByChatRoomIdxOrderByChatRoomIdxAsc(roomIdx)
                .stream()
                .map(ChatRoomMessageResp::fromEntity)
                .collect(Collectors.toList());

        List<ChatHighlightResp> highlights = highlightRepository.findAllByChatRoomIdxOrderByStartTimeAsc(roomIdx)
                .stream()
                .map(ChatHighlightResp::fromEntity)
                .toList();

        return ChatRoomItemResp.fromEntity(chatRoom, messages, highlights);
    }
}
