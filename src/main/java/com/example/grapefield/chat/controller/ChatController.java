package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.model.response.ChatDetailResp;
import com.example.grapefield.chat.model.response.ChatListResp;
import com.example.grapefield.chat.model.response.ChatMessageItemResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "7. 채팅 기능", description = "채팅방 컨트롤러")
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Operation(
            summary = "채팅방 목록 조회",
            description = "모든 채팅방 목록을 조회",
            responses = {@ApiResponse(responseCode = "200", description = "채팅방 목록 응답 예시", content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = ChatListResp.class))
                            )
                    )
            }
    )
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatListResp>> getRoomsList() {
        //swagger를 위한 더미 데이터
        List<ChatListResp> dummyList = List.of(
                ChatListResp.builder()
                        .roomIdx(1L)
                        .roomName("햄이방")
                        .lastMessage("안녕 식아!")
                        .lastMessageTime(LocalDateTime.now().minusMinutes(2))
                        .unreadCount(3)
                        .build(),
                ChatListResp.builder()
                        .roomIdx(2L)
                        .roomName("프로젝트 채팅방")
                        .lastMessage("회의는 3시에 시작해요")
                        .lastMessageTime(LocalDateTime.now().minusHours(1))
                        .unreadCount(0)
                        .build()
        );
        return ResponseEntity.ok(dummyList);
    }

    @GetMapping("/room/{idx}")
    @Operation(summary = "특정 채팅방 메시지 조회", description = "채팅방 Idx를 통해 특정 채팅방의 채팅 내용 확인")
    public ResponseEntity<ChatDetailResp> getRoomDetail(@PathVariable Long idx) {
        //swagger를 위한 더미 데이터
        List<ChatMessageItemResp> messages = List.of(
                ChatMessageItemResp.builder()
                        .userIdx(2L)
                        .nickname("햄이")
                        .content("식아 자니?")
                        .createdAt(LocalDateTime.now().minusMinutes(2))
                        .isHighlighted(false)
                        .build(),

                ChatMessageItemResp.builder()
                        .userIdx(3L)
                        .nickname("식이")
                        .content("아직 안 자~")
                        .createdAt(LocalDateTime.now().minusMinutes(1))
                        .isHighlighted(true)
                        .build()
        );

        ChatDetailResp response = ChatDetailResp.builder()
                .roomIdx(idx)
                .roomName("햄이와 식이의 채팅방")
                .participants(List.of("햄이", "식이"))
                .messages(messages)
                .build();
        return ResponseEntity.ok(response);
    }


}
