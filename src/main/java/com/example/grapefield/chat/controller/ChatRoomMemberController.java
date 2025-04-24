package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.service.ChatRoomMemberService;
import com.example.grapefield.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomMemberController {

    private final ChatRoomMemberService chatRoomMemberService;

    // 채팅방 입장
    @Operation(summary = "채팅방 입장", description = "사용자가 채팅방에 입장하면 참여 정보가 저장, 이미 참여 중이면 아무 처리도 하지 않음")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "채팅방 입장 성공",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "채팅방 입장 완료"))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한이 없는 사용자",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "JWT 인증 실패 또는 미인증 상태"))
            )
    })
    @PostMapping("/join/{roomIdx}")
    public ResponseEntity<String> joinRoom(
            @PathVariable Long roomIdx,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userIdx = userDetails.getUser().getIdx();
        chatRoomMemberService.joinRoom(userIdx, roomIdx);
        return ResponseEntity.ok("채팅방 입장 완료");
    }
    // 채팅방 퇴장
    @Operation(summary = "채팅방 퇴장", description = "채팅방에서 퇴장하면 DB에서 참여 정보가 제거")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "채팅방 퇴장 성공",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "채팅방 퇴장 완료"))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "JWT 인증 실패 또는 미인증 상태"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "이미 퇴장했거나 참여 기록 없음",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "이미 퇴장한 채팅방입니다."))
            )
    })
    @DeleteMapping("/leave/{roomIdx}")
    public ResponseEntity<String> leaveRoom(
            @PathVariable Long roomIdx,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userIdx = userDetails.getUser().getIdx();
        chatRoomMemberService.leaveRoom(userIdx, roomIdx);
        return ResponseEntity.ok("채팅방 퇴장 완료");
    }

}
