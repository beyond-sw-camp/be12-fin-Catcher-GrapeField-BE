package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.service.ChatRoomMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomMemberController {

    private final ChatRoomMemberService chatRoomMemberService;

    @PostMapping("/join/{roomIdx}")
    public ResponseEntity<String> joinRoom(
            @PathVariable Long roomIdx,
            @RequestParam Long userIdx
    ) {
        chatRoomMemberService.joinRoom(userIdx, roomIdx);
        return ResponseEntity.ok("채팅방 입장 완료");
    }
}
