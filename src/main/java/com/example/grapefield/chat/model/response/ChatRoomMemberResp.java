package com.example.grapefield.chat.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "채팅방 참여자 정보 응답 DTO")
public class ChatRoomMemberResp {
    @Schema(description = "사용자 IDX", example = "1")
    private Long userIdx;
    @Schema(description = "사용자 이름", example = "김포도")
    private String username;
    @Schema(description = "해당 사용자가 마지막으로 읽은 시각", example = "2025-04-23T14:30:00")
    private LocalDateTime lastReadAt;
    @Schema(description = "알림음 끄기 여부", example = "true")
    private Boolean mute;
    @Schema(description = "해당 사용자가 마지막으로 활동한 시각", example = "2025-04-24T09:15:00")
    private LocalDateTime lastActiveAt;
}
