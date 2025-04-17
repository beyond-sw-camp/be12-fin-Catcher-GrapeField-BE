package com.example.grapefield.chat.controller;

import com.example.grapefield.chat.model.response.ChatListPageResp;
import com.example.grapefield.chat.model.response.ChatListResp;
import com.example.grapefield.chat.model.response.PopularChatRoomListResp;
import com.example.grapefield.chat.service.ChatRoomListService;
import com.example.grapefield.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/list")
public class ChatRoomListController {

    private final ChatRoomListService chatRoomQueryService;
    private final ChatRoomListService chatRoomListService;

    // 사용자가 참여한 채팅방 목록 (사이드바 )
    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatListResp>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userIdx = userDetails.user().getIdx(); // 여기서 User 꺼내면 됨
        List<ChatListResp> rooms = chatRoomQueryService.getMyRooms(userIdx);
        return ResponseEntity.ok(rooms);
    }


    // 전체 채팅방 리스트
    @GetMapping("/all")
    public ResponseEntity<List<ChatListPageResp>> getAllRooms() {
        return ResponseEntity.ok(chatRoomListService.getAllRooms());
    }

    // 전체 공연 채팅방 리스트 (뮤지컬, 연극, 콘서트)
    @GetMapping("/performance")
    public ResponseEntity<List<ChatListPageResp>> getPerformanceRooms() {
        return ResponseEntity.ok(chatRoomListService.getRoomsByType("performance"));
    }

    // 전체 전시 채팅방 리스트 (전시회, 박람회)
    @GetMapping("/exhibition")
    public ResponseEntity<List<ChatListPageResp>> getExhibitionRooms() {
        return ResponseEntity.ok(chatRoomListService.getRoomsByType("exhibition"));
    }

    // 사용자가 참여한 채팅방 리스트 (전체화면)
    @GetMapping("/my-page")
    public ResponseEntity<List<ChatListPageResp>> getMyPageRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userIdx = userDetails.user().getIdx();
        return ResponseEntity.ok(chatRoomListService.getMyPageRooms(userIdx));
    }

    // 인기 채팅방 목록
    @GetMapping("/all-time-best")
    public ResponseEntity<List<PopularChatRoomListResp>> getAllTimeBestRooms() {
        return ResponseEntity.ok(chatRoomListService.getAllTimeBestRooms());
    }

    // 인기 채팅방 목록
    @GetMapping("/hot-now")
    public ResponseEntity<List<PopularChatRoomListResp>> getHotNowRooms() {
        return ResponseEntity.ok(chatRoomListService.getHotNowRooms());
    }

}
