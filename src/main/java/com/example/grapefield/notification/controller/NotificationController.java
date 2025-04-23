package com.example.grapefield.notification.controller;

import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.notification.model.response.NotificationResp;
import com.example.grapefield.notification.service.NotificationService;
import com.example.grapefield.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notify")
@Tag(name="알림 기능", description = "사용자의 일정을 관리하고 즐겨찾기한 이벤트, 등록한 일정을 알림")
public class NotificationController {
  private final NotificationService notificationService;

  //공연/전시 알림 토글
  @PatchMapping("/event/toggle")
  public ResponseEntity<Boolean> toggleNotify(@RequestParam Long idx, @AuthenticationPrincipal CustomUserDetails principal) {
    if (principal == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); }
    Boolean result = notificationService.toggleNotify(idx, principal.getUser());
    return ResponseEntity.ok(result);
  }

  //사용자의 모든 알림 목록 - DTO로 변환하여 반환
  @GetMapping("/all")
  public ResponseEntity<List<NotificationResp>> getUserNotifications(@AuthenticationPrincipal CustomUserDetails principal) {
    Long userIdx = principal.getUser().getIdx();
    List<NotificationResp> notifications = notificationService.getUserNotifications(userIdx);
    return ResponseEntity.ok(notifications);
  }

  //"지금 보여줄 알림"만 조회
  @GetMapping("/noti")
  public ResponseEntity<List<NotificationResp>> getAvailableNotifications(@AuthenticationPrincipal CustomUserDetails principal) {
    Long userIdx = principal.getUser().getIdx();
    List<NotificationResp> notifications = notificationService.getAvailableNotifications(userIdx);
    return ResponseEntity.ok(notifications);
  }

  // 읽지 않은 알림 목록 - DTO로 변환하여 반환
  @GetMapping("/unread")
  public ResponseEntity<List<NotificationResp>> getUnreadNotifications(@AuthenticationPrincipal CustomUserDetails principal) {
    Long userIdx = principal.getUser().getIdx();
    List<NotificationResp> notifications = notificationService.getUnreadNotifications(userIdx);
    return ResponseEntity.ok(notifications);
  }

  //읽은 알림 목록
  @PutMapping("/{notificationIdx}/read")
  public ResponseEntity<Void> markAsRead(@PathVariable Long notificationIdx) {
    notificationService.markAsRead(notificationIdx);
    return ResponseEntity.ok().build();
  }

  // 모든 알림 읽음 처리 엔드포인트 추가 (프론트엔드에서 사용 중이므로)
  @PostMapping("/read-all")
  public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CustomUserDetails principal) {
    Long userIdx = principal.getUser().getIdx();
    notificationService.markAllAsRead(userIdx);
    return ResponseEntity.ok().build();
  }

  //알림을 클릭해서 삭제(소프트)
  @DeleteMapping("/delete/{notificationIdx}")
  public ResponseEntity<Void> hideNotification(@PathVariable Long notificationIdx) {
    notificationService.hideNotification(notificationIdx);
    return ResponseEntity.ok().build();
  }
}