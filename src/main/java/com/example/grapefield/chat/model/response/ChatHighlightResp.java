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
@Schema(description = "채팅방 하이라이트 구간 응답")
public class ChatHighlightResp {
    @Schema(description = "하이라이트 구간 고유 Idx", example = "101")
    private Long idx;
    @Schema(description = "하이라이트 구간 시작 시간", example = "2024-04-10T10:45:00")
    private LocalDateTime startTime;
    @Schema(description = "하이라이트 구간 종료 시간", example = "2024-04-10T10:45:00")
    private LocalDateTime endTime;
    @Schema(description = "내용 요약 키워드", example = "2024-04-10T10:45:00")
    private String description;
    @Schema(description = "구간 메세지 개수", example = "333")
    private Long messageCnt;

}
