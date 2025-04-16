package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.model.response.ChatRoomItemResp;
import com.example.grapefield.chat.service.ChatQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ChatRoomItemResp> getRoomItem(@PathVariable("roomIdx") int roomIdx) {
        ChatRoomItemResp response = chatQueryService.getChatRoomItem(roomIdx);
        return ResponseEntity.ok(response);
    }

}
