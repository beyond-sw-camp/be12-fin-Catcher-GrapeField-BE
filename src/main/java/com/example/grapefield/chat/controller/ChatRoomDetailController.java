package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.response.ChatHighlightResp;
import com.example.grapefield.chat.model.response.ChatMessageResp;
import com.example.grapefield.chat.model.response.ChatRoomDetailResp;
import com.example.grapefield.chat.model.response.ChatRoomMemberResp;
import com.example.grapefield.chat.service.ChatRoomService;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomDetailController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/{roomIdx}")
    public ChatRoomDetailResp chatRoomDetail(@PathVariable("roomIdx") Long roomIdx) {
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
                        .map(h -> ChatHighlightResp.builder()
                                .idx(h.getIdx())
                                .startTime(h.getStartTime())
                                .endTime(h.getEndTime())
                                .description(h.getDescription())
                                .messageCnt(h.getMessageCnt())
                                .build()).collect(Collectors.toList()))
                .build();
    }
}
