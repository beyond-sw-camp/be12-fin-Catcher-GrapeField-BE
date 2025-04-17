package com.example.grapefield.events;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.common.PageResponse;
import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.request.EventsRegisterReq;
import com.example.grapefield.events.model.response.*;
import com.example.grapefield.events.post.model.request.PostRegisterReq;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
@Tag(name="3. 공연/전시 기능", description = "공연/전시를 등록하고 목록 및 상세 정보를 확인")
public class EventsController {
  private final EventsService eventsService;
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
  public ResponseEntity<PageResponse<EventsListResp>> getEventList(@PageableDefault(page = 0, size = 30) Pageable pageable) {
    Page<Events> eventPage = eventsService.getEventListWithPagination(pageable);
    List<EventsListResp> dtoList = eventPage.stream().map(event -> EventsListResp.builder()
            .idx(event.getIdx())
            .title(event.getTitle())
            .category(event.getCategory())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .posterImgUrl(event.getPosterImgUrl())
            .venue(event.getVenue())
            .build()).toList();
    return ResponseEntity.ok(PageResponse.from(eventPage, dtoList));
  }


  @Operation(summary = "추천, 인기, 신규 한꺼번에 불러오기", description = "메인 페이지에서 사이트에 등록된 공연과 전시의 추천, 인기, 신규 목록을 한꺼번에 불러오기")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/contents/main")
  public ResponseEntity<Map<String, Slice<EventsListResp>>> getMainEvents(@RequestParam String category) {
    Map<String, Slice<EventsListResp>> eventList = eventsService.getMainEvents(category);
    return ResponseEntity.ok(eventList);
  }

  @Operation(summary = "공연/전시 페이지 목록", description = "공연/전시 페이지에서 사이트에 등록된 공연과 전시를 선택한 카테고리에 맞춰 무한스크롤 형식으로 불러오기")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/contents/list")
  public ResponseEntity<Slice<EventsListResp>> getMoreEventList(@PageableDefault(page = 0, size = 30) Pageable pageable, @RequestParam String category, String array) {
    //데이터는 한번에 30개씩, 무한 스크롤 형식, 카테고리 필터 포함, 공연 날짜 기준으로 최신 정렬
    Slice<EventsListResp> eventList = eventsService.getMoreEventList(category, array, pageable);
    return ResponseEntity.ok(eventList);
  }

  @Operation(summary = "오픈예정, 종료예정 한꺼번에 불러오기", description = "메인 페이지에서 사이트에 등록된 공연과 전시의 오픈예정, 종료예정 목록을 한꺼번에 불러오기")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/ticket/main")
  public ResponseEntity<Map<String, Slice<EventsTicketScheduleListResp>>> getMainEventsTicketSchedule() {
    Map<String, Slice<EventsTicketScheduleListResp>> eventList = eventsService.getMainEventsTicketSchedule();
    return ResponseEntity.ok(eventList);
  }

  @Operation(summary = "오픈예정, 종료예정 목록", description = "더보기 눌렀을 때 사이트에 등록된 공연과 전시의 오픈예정, 종료예정을 무한스크롤 형식으로 불러오기")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/ticket/list")
  public ResponseEntity<Slice<EventsTicketScheduleListResp>> getMoreEventsTicketSchedule(@RequestParam String type, @PageableDefault(page = 0, size = 30) Pageable pageable) {
    //데이터는 한번에 30개씩, 무한 스크롤 형식, 카테고리 필터 포함, 공연 날짜 기준으로 최신 정렬
    Slice<EventsTicketScheduleListResp> eventList = eventsService.getMoreEventsTicketSchedule(type, pageable);
    return ResponseEntity.ok(eventList);
  }

  @Operation(summary = "캘린더 이벤트 조회", description = "해당 월의 예매 시작/종료 이벤트를 구분하여 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/calendar")
  public ResponseEntity<Map<String, List<EventsCalendarListResp>>> getCalendarEvents(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date
  ) {
    Map<String, List<EventsCalendarListResp>> eventMap = eventsService.getCalendarEvents(date);
    return ResponseEntity.ok(eventMap);
  }

  @Operation(summary = "캘린더 상세 이벤트 조회", description = "해당 월의 예매 시작/종료 이벤트를 구분하여 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/calendar_detail")
  public ResponseEntity<Map<String, List<EventsDetailCalendarListResp>>> getDetailCalendarEvents(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date
  ) {
    System.out.println("\n컨트롤러 접근 성공\n");
    Map<String, List<EventsDetailCalendarListResp>> eventMap = eventsService.getDetailCalendarEvents(date);
    return ResponseEntity.ok(eventMap);
  }

  //TODO : 캘린더 개별날짜 공연/전시 불러오기
//  @Operation(summary = "특정 날짜 이벤트 조회", description = "특정 날짜의 예매 시작/종료 이벤트를 구분하여 조회합니다")
//  @ApiSuccessResponses
//  @ApiErrorResponses
//  @GetMapping("/calendar/day")
//  public ResponseEntity<Map<String, List<EventsCalendarListResp>>> getDailyEvents(
//      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date
//  ) {
//    Map<String, List<EventsCalendarListResp>> eventMap = eventsService.getEventsForDay(date);
//    return ResponseEntity.ok(eventMap);
//  }

//  @Operation(summary = "추천, 인기, 신규 공연/전시 목록 조회", description = "메인 페이지에 표시되는 추천, 인기, 신규 공연/전시의 목록을 불러와서 조회")
//  @ApiSuccessResponses
//  @ApiErrorResponses
//  @GetMapping("/contents/recommend")
//  public ResponseEntity<Map<String, List<EventsListResp>>> getMainEvents(@RequestParam String category) {
//    //추천(현재 진행중 or 진행 예정인 공연 중에서 즐겨찾기가 많은 이벤트)
//    //인기(진행 여부와 상관 없이 즐겨찾기가 많은 이벤트)
//    //신규(진행 예정인 공연)
//    Map<String, List<EventsListResp>> eventMap = eventsService.getMainEvents(category);
//    return ResponseEntity.ok(eventMap);
//  }


  @Operation(summary = "공연/전시 상세 조회", description = "사이트에 등록된 공연 및 전시의 상세한 정보를 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/{idx}")
  public ResponseEntity<EventsDetailResp> getEventDetail(@PathVariable Long idx) {
    EventsDetailResp response = eventsService.getEventDetail(idx);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "공연/전시 상세 페이지의 상세 이미지 목록 가져오기", description = "공연/전시 상세 페이지에서 하단에 표시될 상세 이미지 목록 가져오기")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/images/{idx}")
  public ResponseEntity<List<EventsImgDetailResp>> getEventDetailImages(@PathVariable Long idx) {
    List<EventsImgDetailResp> response = eventsService.getEventDetailImages(idx);
    return ResponseEntity.ok(response);
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
  @PutMapping("/update/{idx}")
  public ResponseEntity<String> updateComment(@PathVariable Long idx, @RequestBody EventsRegisterReq request, @AuthenticationPrincipal User user) {
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
  @PutMapping("/delete/{idx}")
  public ResponseEntity<String> updateComment(@PathVariable Long idx, @AuthenticationPrincipal User user) {
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

  //TODO : 검색
}
