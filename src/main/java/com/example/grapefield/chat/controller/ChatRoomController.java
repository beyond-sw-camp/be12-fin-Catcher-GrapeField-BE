package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.model.response.ChatListResp;
import com.example.grapefield.chat.service.ChatRoomService;
import com.example.grapefield.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/list")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 사용자가 참여한 채팅방 목록
    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatListResp>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userIdx = userDetails.user().getIdx(); // 여기서 User 꺼내면 됨
        List<ChatListResp> rooms = chatRoomService.getMyRooms(userIdx);
        return ResponseEntity.ok(rooms);
    }


}
