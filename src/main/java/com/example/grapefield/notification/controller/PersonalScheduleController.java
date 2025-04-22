package com.example.grapefield.notification.controller;

import com.example.grapefield.events.review.model.request.ReviewRegisterReq;
import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.request.PersonalScheduleReq;
import com.example.grapefield.notification.service.PersonalScheduleService;
import com.example.grapefield.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/personal_calendar")
@Tag(name="개인 일정 기능", description = "사용자가 개인적인 일정을 등록하거나 수정, 삭제하는 기능")
public class PersonalScheduleController {
  private final PersonalScheduleService personalScheduleService;

  @PostMapping("/register")
  public ResponseEntity<Long> registerSchedule(
      @RequestBody PersonalScheduleReq request, @AuthenticationPrincipal CustomUserDetails principal) {
    if (principal == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); }
    Long scheduleIdx = personalScheduleService.registerSchedule(request, principal.getUser());
    return ResponseEntity.ok(scheduleIdx);
  }

  //TODO : 개인 일정 불러오기
//  @GetMapping("/list")
//  public  ResponseEntity<List<PersonalSchedule>> getSchedules(@AuthenticationPrincipal CustomUserDetails principal) {
//    if (principal == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); }
//
//  }



}
