package com.example.grapefield.chat.model.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Kafka 전송용 채팅 메시지 DTO")
public class ChatMessageKafkaReq {
    @Schema(description = "채팅방 ID", example = "1", required = true)
    private Long roomIdx;
    @Schema(description = "보낸 사용자 ID", example = "3", required = true)
    private Long sendUserIdx;
    @Schema(description = "채팅 메시지 내용", example = "안녕하세요!", required = true)
    private String content;
}
