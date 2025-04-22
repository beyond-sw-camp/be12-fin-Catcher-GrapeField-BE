package com.example.grapefield.notification.controller;

import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.notification.service.NotificationService;
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
@RequestMapping("/notify")
@Tag(name="알림 기능", description = "사용자의 일정을 관리하고 즐겨찾기한 이벤트, 등록한 일정을 알림")
public class NotificationController {
  private final NotificationService notificationService;

  //공연/전시 알림 등록
  @GetMapping("/event")
  public ResponseEntity<Boolean> toggleNotify(@RequestParam Long idx, @AuthenticationPrincipal CustomUserDetails principal) {
    if (principal == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); }
    Boolean result = notificationService.toggleNotify(idx, principal.getUser());
    return  ResponseEntity.ok(result);
  }

  //사용자 개인 일정 알림 목록
  @GetMapping
  public ResponseEntity<List<ScheduleNotification>> getUserNotifications(
      @RequestParam Long userIdx) {
    return ResponseEntity.ok(notificationService.getUserNotifications(userIdx));
  }

  @GetMapping("/unread")
  public ResponseEntity<List<ScheduleNotification>> getUnreadNotifications(@AuthenticationPrincipal CustomUserDetails principal) {
    Long userIdx = principal.getUser().getIdx();
    return ResponseEntity.ok(notificationService.getUnreadNotifications(userIdx));
  }

  @PutMapping("/{notificationId}/read")
  public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
    notificationService.markAsRead(notificationId);
    return ResponseEntity.ok().build();
  }
}
