package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.model.response.ChatRoomItemResp;
import com.example.grapefield.chat.service.ChatQueryService;
import com.example.grapefield.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatQueryController {
    private final ChatQueryService chatQueryService;
    @GetMapping("/rooms/{roomIdx}")
    public ResponseEntity<ChatRoomItemResp> getRoomItem(@PathVariable("roomIdx") Long roomIdx, @AuthenticationPrincipal CustomUserDetails user) {
        ChatRoomItemResp response = chatQueryService.getChatRoomItem(roomIdx, user);
        return ResponseEntity.ok(response);
    }

}
