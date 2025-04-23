package com.example.grapefield.notification.infrastructure.scheduler;

import com.example.grapefield.notification.service.NotificationService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationInitializer {

  private final NotificationService notificationService;

  @PostConstruct
  public void init() {
    notificationService.scheduleAllPendingNotifications();
  }
}
