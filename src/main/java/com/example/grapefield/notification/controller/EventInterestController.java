package com.example.grapefield.notification.controller;

import com.example.grapefield.notification.service.EventsInterestService;
import com.example.grapefield.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/interest")
@Tag(name="즐겨찾기 기능", description = "공연/전시를 즐겨찾기")
public class EventInterestController {
  private EventsInterestService eventsInterestService;

  @GetMapping("/register")
  public ResponseEntity<Boolean> toggleEventInterest(@RequestParam Long idx, @AuthenticationPrincipal CustomUserDetails principal) {
    if (principal == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); }
    Boolean result = eventsInterestService.toggleEventInterest(idx, principal.getUser());
    return  ResponseEntity.ok(result);
  }
}
