package com.example.grapefield.chat.model.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "인기 채팅방 목록 응답 DTO")
public class PopularChatRoomListResp {
    @Schema(description = "채팅방 ID", example = "1")
    private Long roomIdx;
    @Schema(description = "채팅방 이름", example = "연극 꽃의 비밀")
    private String roomName;
    @Schema(description = "장소", example ="세종문화회관")
    private String venue;
    @Schema(description = "참여중인 유저 수", example = "88")
    private int memberCount;
    @Schema(description = "누적 좋아요 수", example = "2300")
    private int heartCount;
}
