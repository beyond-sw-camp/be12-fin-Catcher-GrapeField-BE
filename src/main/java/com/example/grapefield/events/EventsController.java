package com.example.grapefield.events;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.request.EventsRegisterReq;
import com.example.grapefield.events.model.response.EventsDetailResp;
import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.request.PostRegisterReq;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
@Tag(name="3. 공연/전시 기능", description = "공연/전시를 등록하고 목록 및 상세 정보를 확인")
public class EventsController {
  @Operation(summary="공연/전시 등록", description = "공연 및 전시를 등록 합니다(관리자)")
  @ApiResponses(
      @ApiResponse(responseCode = "200", description = "공연/전시 등록 성공",
      content = @Content(mediaType = "text/plain",
          examples = @ExampleObject(value = "공연/전시 정보를 성공적으로 등록"))))
  @ApiErrorResponses
  @PostMapping("/register")
  public ResponseEntity<Long> register(
      @RequestBody EventsRegisterReq request) {
    //TODO: 공연/전시 idx를 반환하여 등록된 공연/전시 상세 페이지로 이동되도록 추후 수정
    return ResponseEntity.ok(1L);
  }

  @Operation(summary = "공연/전시 목록 조회", description = "사이트에 등록된 공연과 전시를 목록으로 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/list")
  public ResponseEntity<List<EventsListResp>> getEventsList(
      @AuthenticationPrincipal User user) {
    List<EventsListResp> dummyList = List.of(
        new EventsListResp(1L,"웃는 남자", EventCategory.MUSICAL, LocalDateTime.now(), LocalDateTime.now(), "/sample/images/poster/poster1.jpg", "예술의전당 오페라극장"),
        new EventsListResp(2L,"우는 남자", EventCategory.MUSICAL, LocalDateTime.now(), LocalDateTime.now(), "/sample/images/poster/poster1.jpg", "예술의전당 오페라극장")
    );
    return ResponseEntity.ok(dummyList);
  }

  //(캘린더용)날짜 선택하면 해당 날짜의 공연/전시 불러오기
  @Operation(summary = "특정 날짜의 공연/전시 조회", description = "입력한 날짜에 진행 중인 공연/전시를 목록으로 반환합니다. (캘린더 전용)")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/calendar")
  public ResponseEntity<List<EventsListResp>> getEventsByDate(
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date
  ) {
    List<EventsListResp> dummyList = List.of(
            new EventsListResp(1L,"웃는 남자", EventCategory.MUSICAL, LocalDateTime.now(), LocalDateTime.now(), "/sample/images/poster/poster1.jpg", "예술의전당 오페라극장"),
            new EventsListResp(2L,"우는 남자", EventCategory.MUSICAL, LocalDateTime.now(), LocalDateTime.now(), "/sample/images/poster/poster1.jpg", "예술의전당 오페라극장")
    );
    return ResponseEntity.ok(dummyList);
  }


  @Operation(summary = "공연/전시 상세 조회", description = "사이트에 등록된 공연 및 전시의 상세한 정보를 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/{eventsIdx}")
  public ResponseEntity<EventsDetailResp> getEventsDetail(@PathVariable Long eventsIdx
      ) {
    EventsDetailResp dummy = new EventsDetailResp();
    return ResponseEntity.ok().body(dummy);
  }

  @Operation(summary = "공연/전시 수정", description = "등록된 공연/전시의 내용을 수정(관리자만 가능)")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "수정 성공",
                  content = @Content(mediaType = "text/plain",
                          examples = @ExampleObject(value = "수정 성공"))),
          @ApiResponse(responseCode = "403", description = "관리자만 가능한 기능입니다.",
                  content = @Content(mediaType = "text/plain",
                          examples = @ExampleObject(value = "권한이 없습니다.")))
  })
  @ApiErrorResponses
  @PutMapping("/update/{eventsIdx}")
  public ResponseEntity<String> updateComment(@PathVariable Long eventsIdx, @RequestBody EventsRegisterReq request, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok("수정 성공");
  }

  @Operation(summary = "공연/전시 삭제", description = "등록된 공연/전시를 삭제(관리자만 가능, 실제로 DB상에서는 삭제하지 않고 is_visible을 false로 바꿈)")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "삭제 성공",
                  content = @Content(mediaType = "text/plain",
                          examples = @ExampleObject(value = "삭제 성공"))),
          @ApiResponse(responseCode = "403", description = "관리자만 가능한 기능입니다.",
                  content = @Content(mediaType = "text/plain",
                          examples = @ExampleObject(value = "권한이 없습니다.")))
  })
  @ApiErrorResponses
  @PutMapping("/delete/{eventsIdx}")
  public ResponseEntity<String> updateComment(@PathVariable Long eventsIdx, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok("게시글 삭제 성공");
  }

  //TODO: Review(한줄평, 별점) 등록
  @Operation(summary="게시글 등록", description = "공연 및 전시 게시판에서 게시글을 등록")
  @ApiResponses(
      @ApiResponse(responseCode = "200", description = "게시글 등록 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "게시글을 성공적으로 등록"))))
  @ApiErrorResponses
  @PostMapping("/review/register")
  public ResponseEntity<Long> postRegister(
      @RequestBody PostRegisterReq request, @AuthenticationPrincipal User user) {
    //TODO: 게시글 idx를 반환하여 등록된 게시글로 페이지 이동되도록 추후 수정
    return ResponseEntity.ok(1L);
  }

  //TODO: Review(한줄평, 별점) 목록 조회

  //TODO: 공지사항, Qna, FaQ,
}
