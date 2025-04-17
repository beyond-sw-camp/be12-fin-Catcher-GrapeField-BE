package com.example.grapefield.chat.model.response;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.events.model.entity.Events;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "전체 채팅방 리스트 페이지 응답 DTO")
public class ChatListPageResp {
    @Schema(description = "채팅방 ID", example = "1")
    private Long roomIdx;

    @Schema(description = "채팅방 이름", example = "오페라의 유령 채팅방")
    private String roomName;

    @Schema(description = "이벤트 설명", example = "마스크 의상이 바뀌었어요")
    private String description;

    @Schema(description = "이벤트 시작일", example = "2025-04-01T00:00:00")
    private LocalDateTime eventStartDate;

    @Schema(description = "이벤트 종료일", example = "2025-04-30T23:59:59")
    private LocalDateTime eventEndDate;

    @Schema(description = "현재 참여자 수", example = "203")
    private int participantCount;

    @Schema(description = "이벤트 포스터 이미지 URL", example = "images/2025_라이온킹_포스터.jpg")
    private String posterImgUrl;

    public static ChatListPageResp from(ChatRoom room, int participantCount) {
        Events event = room.getEvents();

        return ChatListPageResp.builder()
                .roomIdx(room.getIdx())
                .roomName(room.getRoomName())
                .description(event.getDescription())
                .eventStartDate(event.getStartDate())
                .eventEndDate(event.getEndDate())
                .participantCount(participantCount)
                .posterImgUrl(event.getPosterImgUrl())
                .build();
    }
}
