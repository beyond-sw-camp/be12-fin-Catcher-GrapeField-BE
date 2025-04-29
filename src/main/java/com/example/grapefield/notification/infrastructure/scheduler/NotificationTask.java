package com.example.grapefield.notification.infrastructure.scheduler;

import com.example.grapefield.notification.infrastructure.sender.NotificationSender;
import com.example.grapefield.notification.model.entity.ScheduleNotification;

public class NotificationTask implements Runnable {
  private final ScheduleNotification notification;
  private final NotificationSender notificationSender;

  public NotificationTask(ScheduleNotification notification, NotificationSender notificationSender) {
    this.notification = notification;
    this.notificationSender = notificationSender;
  }

  @Override
  public void run() {
    notificationSender.sendNotification(notification);
  }
}