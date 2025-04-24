package com.example.grapefield.chat.controller;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.chat.model.response.ChatListPageResp;
import com.example.grapefield.chat.model.response.ChatListResp;
import com.example.grapefield.chat.model.response.PopularChatRoomListResp;
import com.example.grapefield.chat.service.ChatRoomListService;
import com.example.grapefield.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/list")
@Tag(name="채팅방 리스트 ", description = "메인의 사이드바와 채팅 리스트 페이지에서 채팅방 리스트 조회 기능")
public class ChatRoomListController {

    private final ChatRoomListService chatRoomListService;

    // 사용자가 참여한 채팅방 목록 (사이드바 )
    @Operation(summary = "사이드바에서 로그인 한 사용자가 참여하고 있는 채팅방 리스트 조회",
            description = "사이드바에서 로그인 한 사용자가 참여하고 있는" +
                    " 채팅방 목록을 조회, 채팅방 이름, 최근 채팅 내용, 참여자 수, 이벤트 시작, 종료일을 리스트 형식으로 반환하여 조회 ")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatListResp>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userIdx = userDetails.getUser().getIdx(); // 여기서 User 꺼내면 됨
        List<ChatListResp> rooms = chatRoomListService.getMyRooms(userIdx);
        return ResponseEntity.ok(rooms);
    }


    // 전체 채팅방 리스트
    @Operation(summary = "전체 채팅방 리스트 조회(chatroom_Idx 오름차순)",
            description = "채팅방 리스트에서 이벤트 포스터 이미지, 채팅방 이름, 참여자수, 이벤트 시작일, 종료일을 리스트 형식으로 반환하여 조회")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/all")
    public ResponseEntity<Slice<ChatListPageResp>> getAllRooms(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(chatRoomListService.getAllRooms(pageable));
    }

    // 전체 공연 채팅방 리스트 (뮤지컬, 연극, 콘서트)
    @Operation(summary = "공연 전체 채팅방 목록 조회(chatroom_Idx 오름차순)",
            description = "뮤지컬, 연극, 콘서트를 합친 이벤트 포스터 이미지, 채팅방 이름, 참여자수, 이벤트 시작일, 종료일을 리스트 형식으로 반환하여 조회")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/performance")
    public ResponseEntity<Slice<ChatListPageResp>> getPerformanceRooms(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(chatRoomListService.getRoomsByType("performance", pageable));
    }

    // 전체 전시 채팅방 리스트 (전시회, 클래식)
    @Operation(summary = "공연 전체 채팅방 목록 조회(chatroom_Idx 오름차순)",
            description = "전시회, 클래식을 합친 이벤트 포스터 이미지, 채팅방 이름, 참여자수, 이벤트 시작일, 종료일을 리스트 형식으로 반환하여 조회")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/exhibition")
    public ResponseEntity<Slice<ChatListPageResp>> getExhibitionRooms(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(chatRoomListService.getRoomsByType("exhibition", pageable));
    }

    // 사용자가 참여한 채팅방 리스트 (전체화면)
    @Operation(summary = "전체 채팅방 리스트에서 로그인 한 사용자가 참여하는 채팅방 목록을 참여 날짜 순으로 조회",
            description = "전체 채팅방 리스트 로그인 한 사용자가 참여하는" +
                    " 채팅방 목록을 조회 이벤트 포스터 이미지, 채팅방 이름, 최근 채팅 내용, 참여자 수, 이벤트 시작, 종료일을 리스트 형식으로 반환하여 조회")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/my-page")
    public ResponseEntity<Slice<ChatListPageResp>> getMyPageRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long userIdx = userDetails.getUser().getIdx();
        return ResponseEntity.ok(chatRoomListService.getMyPageRooms(userIdx, pageable));
    }

    // 전체 채팅방 리스트 인기순 정렬
    @Operation(summary = "전체 채팅방 리스트 인기순 조회(heart_cnt 내림차순)",
            description = "이벤트 포스터 이미지, 채팅방 이름, 참여자수, 이벤트 시작일, 종료일을 리스트 형식으로 반환하여 조회")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/popular")
    public ResponseEntity<Slice<ChatListPageResp>> getPopularRooms(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(chatRoomListService.getPopularRooms(pageable));
    }

    // 인기 채팅방 목록
    @Operation(summary = "전체 채팅방 목록 인기순 상위 5개 조회(heart_cnt 내림차순)",
            description = "채팅방 이름, 참여자수, 하트수, 이벤트 장소를 리스트 형식으로 반환하여 조회")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/all-time-best")
    public ResponseEntity<List<PopularChatRoomListResp>> getAllTimeBestRooms() {
        return ResponseEntity.ok(chatRoomListService.getAllTimeBestRooms());
    }

    // 인기 채팅방 목록
    @Operation(summary = "전체 채팅방 목록 인기순 상위 5개 조회(heart_cnt 내림차순)",
            description = "채팅방 이름, 참여자수, 하트수, 이벤트 장소를 리스트 형식으로 반환하여 조회")
    @ApiSuccessResponses
    @ApiErrorResponses
    @GetMapping("/hot-now")
    public ResponseEntity<List<PopularChatRoomListResp>> getHotNowRooms() {
        return ResponseEntity.ok(chatRoomListService.getHotNowRooms());
    }

}
