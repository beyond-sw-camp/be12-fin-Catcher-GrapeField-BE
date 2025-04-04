package com.example.demo.events;

import com.example.demo.base.ApiErrorResponses;
import com.example.demo.base.ApiSuccessResponses;
import com.example.demo.events.model.entity.EventCategory;
import com.example.demo.events.model.request.EventsRegisterReq;
import com.example.demo.events.model.response.EventsDetailResp;
import com.example.demo.events.model.response.EventsListResp;
import com.example.demo.user.model.entity.User;
import com.example.demo.user.model.response.UserInfoListResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
@Tag(name="공연/전시 기능", description = "공연/전시를 등록하고 목록 및 상세 정보를 확인")
public class EventsController {
  @Operation(summary="공연/전시 등록", description = "공연 및 전시를 등록 합니다(관리자)")
  @ApiResponses(
      @ApiResponse(responseCode = "200", description = "공연/전시 등록 성공",
      content = @Content(mediaType = "text/plain",
          examples = @ExampleObject(value = "공연/전시 정보를 성공적으로 등록"))))
  @ApiErrorResponses
  @PostMapping("/register")
  public ResponseEntity<String> register(
      @RequestBody EventsRegisterReq request) {
    return ResponseEntity.ok("등록 성공");
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

  @Operation(summary = "공연/전시 상세 조회", description = "사이트에 등록된 공연 및 전시의 상세한 정보를 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/{eventsIdx}")
  public ResponseEntity<EventsDetailResp> getEventsDetail(@PathVariable Long eventsIdx
      ) {
    EventsDetailResp dummy = new EventsDetailResp();
    return ResponseEntity.ok().body(dummy);
  }
}
