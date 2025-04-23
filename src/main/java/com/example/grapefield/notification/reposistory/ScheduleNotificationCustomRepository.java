package com.example.grapefield.notification.reposistory;

import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.notification.model.response.NotificationResp;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleNotificationCustomRepository {
    //NotificationService
    //모든 알림 정보 가져오기
    List<NotificationResp> findNotificationsByUserIdx(Long userIdx);
    //읽지 않은 알림 정보 가져오기
    List<NotificationResp> findUnreadNotificationsByUserIdx(Long userIdx);
    //모든 알림 읽음 처리(벌크 업데이트)
    void markAllAsReadByUserId(Long userId);

  List<NotificationResp> findAvailableNotifications(Long userIdx);

  //모든 알림을 숨김 처리
  void hideAllNotification(Long userIdx);
}
