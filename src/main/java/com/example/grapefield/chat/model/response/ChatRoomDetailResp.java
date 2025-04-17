package com.example.grapefield.chat.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "채팅방 상세 응답 DTO")
public class ChatRoomDetailResp {
    @Schema(description = "채팅방 ID", example = "1")
    private Long roomIdx;
    @Schema(description = "채팅방 이름", example = "햄이와 식이의 모험")
    private String roomName;

    @Schema(description = "createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "누적 좋아요 수")
    private Long heartCnt;
    @Schema(description = "채팅 메시지 목록")
    private List<ChatMessageResp> messageList;
    @Schema(description = "참여자 닉네임 목록", example = "[\"햄이\", \"식이\"]")
    private List<ChatRoomMemberResp> memberList;
    @Schema(description = "하이라이트 구간 목록")
    private List<ChatHighlightResp> highlightList;
}
