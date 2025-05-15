package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.ChatroomMember;
import com.example.grapefield.chat.model.response.ChatListPageResp;
import com.example.grapefield.chat.model.response.ChatListResp;
import com.example.grapefield.chat.model.response.PopularChatRoomListResp;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomMemberRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.repository.EventsRepository;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomListService {
    private final ChatRoomMemberRepository memberRepository;
    private final ChatMessageCurrentRepository currentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberService chatRoomMemberService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate; //⭐ Redis 적용 코드
    private final EventsRepository eventsRepository;


    // 사용자가 참여한 채팅방 목록 불러오기(사이드바 채팅)
    public List<ChatListResp> getMyRooms(Long userIdx) {
        List<ChatroomMember> members = memberRepository.findByUser_Idx(userIdx);
        List<ChatRoom> rooms = members.stream().map(ChatroomMember::getChatRoom).toList();

//        Map<Long, Integer> participantCountMap = chatRoomMemberService.getParticipantCountMap();
        Map<Long, ChatMessageCurrent> lastMsgMap = currentRepository.findLatestMessagesByRooms(rooms).stream()
                .collect(Collectors.toMap(msg -> msg.getChatRoom().getIdx(), msg -> msg));

        return members.stream().map(member -> {
            ChatRoom room = member.getChatRoom();
            ChatMessageCurrent lastMsg = lastMsgMap.get(room.getIdx());
            int unreadCount = currentRepository.countByChatRoomAndCreatedAtAfter(room, member.getLastReadAt());

            // 개별 채팅방의 참여자 수만 Redis에서 조회
            int participantCount = chatRoomMemberService.getParticipantCount(room.getIdx());

            return ChatListResp.from(room, lastMsg, unreadCount, participantCount);
        }).toList();
    }

    // 전체 채팅방 목록
    public Slice<ChatListPageResp> getAllRooms(Pageable pageable) {
        Slice<ChatRoom> rooms = chatRoomRepository.findAllWithEvents(pageable);

        Map<Long, Integer> participantCountMap = chatRoomMemberService.getParticipantCountMap();

        return rooms.map(room -> ChatListPageResp.from(
                room,
                participantCountMap.getOrDefault(room.getIdx(), 0)
        ));
    }


    // 이벤트 카테고리 필터링
    public Slice<ChatListPageResp> getRoomsByCategory(EventCategory category, Pageable pageable) {
        Slice<ChatRoom> rooms = chatRoomRepository.findChatRoomsByCategory(category, pageable);
        Map<Long, Integer> participantCountMap = chatRoomMemberService.getParticipantCountMap();

        return rooms.map(room -> ChatListPageResp.from(
                room,
                participantCountMap.getOrDefault(room.getIdx(), 0)
        ));
    }

    // 전체 채팅방 인기순서
    public Slice<ChatListPageResp> getPopularRooms(Pageable pageable) {
        Slice<ChatRoom> rooms = chatRoomRepository.findAllOrderByHeartCnt(pageable);
        Map<Long, Integer> participantCountMap = chatRoomMemberService.getParticipantCountMap();

        return rooms.map(room -> ChatListPageResp.from(
                room,
                participantCountMap.getOrDefault(room.getIdx(), 0)
        ));
    }

    // 내가 참여한 채팅방 목록 (채팅 전체화면 페이지)
    public Slice<ChatListPageResp> getMyPageRooms(List<ChatRoom> myRooms, Pageable pageable) {
        Map<Long, Integer> participantCountMap = chatRoomMemberService.getParticipantCountMap();

        List<ChatListPageResp> result = myRooms.stream()
                .map(room -> ChatListPageResp.from(
                        room,
                        participantCountMap.getOrDefault(room.getIdx(), 0)
                ))
                .collect(Collectors.toList());

        // 슬라이스 형태로 자르기
        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = Math.min(start + pageable.getPageSize(), result.size());
        boolean hasNext = end < result.size();

        return new org.springframework.data.domain.SliceImpl<>(
                result.subList(start, end), pageable, hasNext
        );
    }
    public Slice<ChatListPageResp> getMyPageRooms(Long userIdx, Pageable pageable) {
        List<ChatroomMember> members = memberRepository.findByUser_Idx(userIdx);
        List<ChatRoom> myRooms = members.stream().map(ChatroomMember::getChatRoom).toList();
        return getMyPageRooms(myRooms, pageable); // 기존 메서드 재활용
    }

    // 메인화면 인기 채팅방 목록 5개
    public List<PopularChatRoomListResp> getAllTimeBestRooms() {
        Pageable pageable = PageRequest.of(0, 5);
        List<ChatRoom> chatRooms = chatRoomRepository.findTop10ByHeartCnt(pageable);

        return chatRooms.stream()
                .map(c -> PopularChatRoomListResp.builder()
                        .roomIdx(c.getIdx())
                        .roomName(c.getRoomName())
                        .venue(c.getEvents().getVenue())
                        .heartCount(c.getHeartCnt().intValue())
                        .memberCount(c.getMemberList() == null ? 0 : c.getMemberList().size())
                        .build())
                .toList();
    }

    public List<PopularChatRoomListResp> getHotNowRooms() {
        Pageable pageable = PageRequest.of(0, 5);
        List<ChatRoom> chatRooms = chatRoomRepository.findTop10ByRecentEventsAndHeartCnt(pageable);

        return chatRooms.stream()
                .map(c -> PopularChatRoomListResp.builder()
                        .roomIdx(c.getIdx())
                        .roomName(c.getRoomName())
                        .venue(c.getEvents().getVenue())
                        .heartCount(c.getHeartCnt().intValue())
                        .memberCount(c.getMemberList() == null ? 0 : c.getMemberList().size())
                        .build())
                .toList();
    }


    public List<PopularChatRoomListResp> getHotNowRoomsRedis() {
        Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores("hot:hearts", 0, 4);

        if (tuples == null || tuples.isEmpty()) {
            log.info("🔔 최근 ♥️증감 이벤트 없음: tuples == null || tuples.isEmpty()");
            Pageable pageable = PageRequest.of(0, 5);
            List<ChatRoom> chatRooms = chatRoomRepository.findTop10ByRecentEventsAndHeartCnt(pageable);

            return chatRooms.stream()
                    .map(c -> PopularChatRoomListResp.builder()
                            .roomIdx(c.getIdx())
                            .roomName(c.getRoomName())
                            .venue(c.getEvents().getVenue())
                            .heartCount(c.getHeartCnt().intValue())
                            .memberCount(c.getMemberList() == null ? 0 : c.getMemberList().size())
                            .build())
                    .toList();
            // return List.of(); // 최근 이벤트 없음
        }

        List<Long> roomIdxs = tuples.stream()
                .map(t -> Long.valueOf(t.getValue().toString()))
                .collect(Collectors.toList());
        Map<Long, ChatRoom> roomsMap = chatRoomRepository.findAllById(roomIdxs).stream()
                .collect(Collectors.toMap(ChatRoom::getIdx, Function.identity()));

        return tuples.stream().map(t -> {
            Long idx = Long.valueOf(t.getValue().toString());
            ChatRoom room = roomsMap.get(idx);
            Events events = eventsRepository.findById(idx).orElse(null);
            // int heartCount = Objects.requireNonNull(t.getScore()).intValue(); // 최근 윈도우에 있는 방의 heartCnt값으로 가져옴
            return PopularChatRoomListResp.builder()
                    .roomIdx(idx)
                    .roomName(room.getRoomName())
                    .venue(Objects.requireNonNull(events).getVenue())
                    .memberCount(room.getMemberList() == null ? 0 : room.getMemberList().size())
                    .heartCount(room.getHeartCnt().intValue())
                    .build();
        }).toList();
    }


}

