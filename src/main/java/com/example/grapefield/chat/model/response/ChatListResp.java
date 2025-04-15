package com.example.grapefield.chat.model.response;

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
    @Schema(description = "이벤트 포스터 이미지 URL", example = "https://example.com/poster.jpg")
    private String eventPosterUrl;
    @Schema(description = "이벤트 시작 날짜", example = "2025-04-04T00:00:00")
    private LocalDateTime eventStartDate;
    @Schema(description = "이벤트 종료 날짜", example = "2025-04-30T23:59:59")
    private LocalDateTime eventEndDate;

}
