package com.example.grapefield.chat.model.response;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.events.model.entity.Events;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "채팅방 목록 응답 DTO")
public class ChatListResp {
    @Schema(description = "채팅방 ID", example = "1")
    private Long roomIdx;
    @Schema(description = "채팅방 이름", example = "햄이방")
    private String roomName;
    @Schema(description = "최근 메시지", example = "안녕 식아!")
    private String lastMessage;
    @Schema(description = "최근 메시지 시간", example = "2025-04-10T12:00:00")
    private LocalDateTime lastMessageTime;
    @Schema(description = "읽지 않은 메시지 수", example = "2")
    private int unreadCount;
    @Schema(description = "이벤트 시작 날짜", example = "2025-04-04T00:00:00")
    private LocalDateTime eventStartDate;
    @Schema(description = "이벤트 종료 날짜", example = "2025-04-30T23:59:59")
    private LocalDateTime eventEndDate;
    @Schema(description = "참여중인 유저 수", example = "257")
    private int participantCount;

    public static ChatListResp from(ChatRoom room, ChatMessageCurrent lastMsg, int unreadCount, int participantCount) {
        Events event = room.getEvents();
        return ChatListResp.builder()
                .roomIdx(room.getIdx())
                .roomName(room.getRoomName())
                .lastMessage(lastMsg != null ? lastMsg.getContent() : "")
                .lastMessageTime(lastMsg != null ? lastMsg.getCreatedAt() : null)
                .unreadCount(unreadCount)
                .eventStartDate(event.getStartDate())
                .eventEndDate(event.getEndDate())
                .participantCount(participantCount)
                .build();
    }


}
