package com.example.grapefield.chat.model.request;
import com.example.grapefield.chat.model.entity.ChatMessageBase;
import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Kafka 전송용 채팅 메시지 DTO")
public class ChatMessageKafkaReq {
    @Schema(description = "채팅방 ID", example = "1", required = true)
    private Long roomIdx;
    @Schema(description = "보낸 사용자 ID", example = "3", required = true)
    private Long sendUserIdx;
    @Schema(description = "채팅 메시지 내용", example = "안녕하세요!", required = true)
    private String content;

    public ChatMessageCurrent toEntity(ChatMessageBase base, ChatRoom room, User user) {
        return ChatMessageCurrent.builder()
                .base(base)
                .chatRoom(room)
                .user(user)
                .content(this.content)
                .createdAt(base.getCreatedAt())
                .isHighlighted(false)
                .build();
    }
}
