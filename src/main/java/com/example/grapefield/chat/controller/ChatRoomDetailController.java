package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.response.*;
import com.example.grapefield.chat.service.ChatMessageService;
import com.example.grapefield.chat.service.ChatRoomService;
import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "7-1. 채팅방 기능", description = "채팅방 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomDetailController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @GetMapping("/{roomIdx}")
    @Operation(summary = "특정 채팅방 메시지 조회", description = "채팅방 Idx를 통해 특정 채팅방의 채팅 내용 확인")
    public ChatRoomDetailResp getChatRoomDetail(@PathVariable("roomIdx") Long roomIdx,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChatRoom room = chatRoomService.findByIdx(roomIdx);
        return ChatRoomDetailResp.builder()
                .roomIdx(roomIdx)
                .roomName(room.getRoomName())
                .createdAt(room.getCreatedAt())
                .heartCnt(room.getHeartCnt())
                .messageList(room.getCurrentMessageList().stream()
                        .map(message -> ChatMessageResp.builder()
                                .messageIdx(message.getMessageIdx())
                                .userIdx(message.getUser().getIdx())
                                .roomIdx(roomIdx)
                                .username(message.getUser().getUsername())
                                .profileImageUrl(message.getUser().getProfileImg())
                                .content(message.getContent())
                                .createdAt(message.getCreatedAt())
                                .isHighlighted(message.getIsHighlighted())
                                .build()).collect(Collectors.toList()))
                .memberList(room.getMemberList().stream()
                        .map(member -> {
                            User user = member.getUser();
                            return ChatRoomMemberResp.builder()
                                    .userIdx(user.getIdx())
                                    .username(user.getUsername())
                                    .lastReadAt(member.getLastReadAt())
                                    .mute(member.getMute())
                                    .lastActiveAt(member.getLastActiveAt())
                                    .build();}).collect(Collectors.toList()))
                .highlightList(room.getHighlightList().stream()
                        .filter(h -> h.getMessage() != null) // 하이라이트가 참조하는 시작메세지idx 값 - null 체크
                        .map(h -> ChatHighlightResp.builder()
                                .idx(h.getIdx())
                                .messageIdx(h.getMessage().getMessageIdx())
                                .startTime(h.getStartTime())
                                .endTime(h.getEndTime())
                                .description(h.getDescription())
                                .messageCnt(h.getMessageCnt())
                                .build()).collect(Collectors.toList()))
                .build();
    }
    /**
     * 메시지 무한 로딩(페이징)용 엔드포인트
     * GET /chat/{roomIdx}/messages?page=0&size=20
     */
    @GetMapping("/{roomIdx}/messages")
    public ChatMessagePageResp getMessagesInPages(
            @PathVariable("roomIdx") Long roomIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        Page<ChatMessageCurrent> messagePage = chatRoomService.getPaginatedMessages(roomIdx, page, size);
        List<ChatMessageResp> dtoList = messagePage.getContent().stream()
                .map(msg -> ChatMessageResp.builder()
                        .messageIdx(msg.getMessageIdx())
                        .userIdx(msg.getUser().getIdx())
                        .roomIdx(roomIdx)
                        .username(msg.getUser().getUsername())
                        .profileImageUrl(msg.getUser().getProfileImg())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .isHighlighted(msg.getIsHighlighted())
                        .build())
                .collect(Collectors.toList());

        return new ChatMessagePageResp(dtoList, messagePage.hasNext());
    }
}
