package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.response.ChatHighlightResp;
import com.example.grapefield.chat.model.response.ChatMessageResp;
import com.example.grapefield.chat.model.response.ChatRoomDetailResp;
import com.example.grapefield.chat.model.response.ChatRoomMemberResp;
import com.example.grapefield.chat.service.ChatRoomMemberService;
import com.example.grapefield.chat.service.ChatRoomService;
import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Tag(name = "7-1. 채팅방 기능", description = "채팅방 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomDetailController {
    private final ChatRoomService chatRoomService;
    private final ChatRoomMemberService chatRoomMemberService;

    @GetMapping("/{roomIdx}")
    @Operation(summary = "특정 채팅방 메시지 조회", description = "채팅방 Idx를 통해 특정 채팅방의 채팅 내용 확인")
    public ChatRoomDetailResp chatRoomDetail(@PathVariable("roomIdx") Long roomIdx,
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
}
