package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.ChatroomMember;
import com.example.grapefield.chat.model.response.ChatListPageResp;
import com.example.grapefield.chat.model.response.ChatListResp;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomMemberRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.events.model.entity.EventCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomListService {
    private final ChatRoomMemberRepository memberRepository;
    private final ChatMessageCurrentRepository currentRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 사용자가 참여한 채팅방 목록 불러오기(사이드바 채팅)
    public List<ChatListResp> getMyRooms(Long userIdx) {
        List<ChatroomMember> members = memberRepository.findByUser_Idx(userIdx);

        return members.stream().map(member -> {
            ChatRoom room = member.getChatRoom();
            ChatMessageCurrent lastMsg = currentRepository.findTopByChatRoomOrderByCreatedAtDesc(room);
            int unreadCount = currentRepository.countByChatRoomAndCreatedAtAfter(room, member.getLastReadAt());
            int participantCount = memberRepository.countByChatRoom(room);

            return ChatListResp.from(room, lastMsg, unreadCount, participantCount);
        }).toList();
    }

    // 전체 채팅방 목록
    public List<ChatListPageResp> getAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> ChatListPageResp.from(room, memberRepository.countByChatRoom(room)))
                .toList();
    }

    // 공연 / 전시 각각 채팅방 목록
    public List<ChatListPageResp> getRoomsByType(String type) {
        List<EventCategory> typeList = switch (type) {
            case "performance" -> List.of(EventCategory.MUSICAL, EventCategory.PLAY, EventCategory.CONCERT);
            case "exhibition" -> List.of(EventCategory.EXHIBITION, EventCategory.FAIR);
            default -> throw new IllegalArgumentException("유효하지 않은 type: " + type);
        };

        return chatRoomRepository.findChatRoomsByCategoryIn(typeList).stream()
                .map(room -> ChatListPageResp.from(room, memberRepository.countByChatRoom(room)))
                .toList();
    }

    // 내가 참여한 채팅방 목록 (채팅 전체화면 페이지)
    public List<ChatListPageResp> getMyPageRooms(Long userIdx) {
        return memberRepository.findByUser_Idx(userIdx).stream()
                .map(member -> ChatListPageResp.from(member.getChatRoom(), memberRepository.countByChatRoom(member.getChatRoom())))
                .toList();
    }

}
