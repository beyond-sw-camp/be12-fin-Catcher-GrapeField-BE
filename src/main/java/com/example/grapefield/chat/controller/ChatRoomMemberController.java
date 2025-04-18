package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.service.ChatRoomMemberService;
import com.example.grapefield.user.CustomUserDetails;
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

}
