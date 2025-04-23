package com.example.grapefield.notification.controller;

import com.example.grapefield.events.review.model.request.ReviewRegisterReq;
import com.example.grapefield.notification.model.response.EventInterestCalendarResp;
import com.example.grapefield.notification.service.CalendarFacadeService;
import com.example.grapefield.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/calendar")
@Tag(name="마이페이지 캘린더 관리", description = "마이페이지에 표시되는 사용자 캘린더 기능")
public class CalendarController {
  private final CalendarFacadeService calendarFacadeService;

  @GetMapping("/list")
  public ResponseEntity<Map<String, Object>> getAllCalendar(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userIdx = userDetails.getUser().getIdx();
    Map<String, Object> calendarResp = calendarFacadeService.getAllCalendar(date, userIdx);

    return ResponseEntity.ok(calendarResp);
  }

  @GetMapping("/interest")
  public ResponseEntity<List<EventInterestCalendarResp>> getInterestEvents(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date, @AuthenticationPrincipal CustomUserDetails userDetails){
    Long userIdx = userDetails.getUser().getIdx();
    List<EventInterestCalendarResp> interestCalendarList = calendarFacadeService.getMyInterestedEvents(userIdx,date);
    return ResponseEntity.ok(interestCalendarList);
  }
}
