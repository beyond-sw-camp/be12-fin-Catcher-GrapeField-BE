package com.example.grapefield.notification.reposistory;

import com.example.grapefield.notification.model.entity.EventsInterest;
import com.example.grapefield.notification.model.entity.ScheduleNotification;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleNotificationRepository extends JpaRepository<ScheduleNotification, Long> {
  //NotificationService
  List<ScheduleNotification> findByUserIdxOrderByNotificationTimeDesc(Long userIdx);
  List<ScheduleNotification> findByUserIdxAndIsReadFalseOrderByNotificationTimeDesc(Long userIdx);
  List<ScheduleNotification> findByNotificationTimeAfter(LocalDateTime time);

  //NotificationScheduler
  List<ScheduleNotification> findByNotificationTimeAfterAndIsReadFalse(LocalDateTime time);
  List<ScheduleNotification> findByEventsInterestIdx(Long eventsInterestIdx);

  boolean existsByUserAndEventsInterest(User user, EventsInterest interest);
  void deleteByUserAndEventsInterest(User user, EventsInterest interest);
}
