package com.example.grapefield.notification.service;

import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.request.PersonalScheduleReq;
import com.example.grapefield.notification.model.response.PersonalScheduleResp;
import com.example.grapefield.notification.reposistory.PersonalScheduleRepository;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalScheduleService {
  private final PersonalScheduleRepository personalScheduleRepository;
  private final NotificationService notificationService;

  public Long registerSchedule(PersonalScheduleReq request, User user) {
    //일정 생성
    PersonalSchedule schedule = PersonalSchedule.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .isNotify(request.getIsNotify())
        .startDate(request.getStartDate())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .user(user)
        .build();
    //일정 저장
    PersonalSchedule saveSchedule = personalScheduleRepository.save(schedule);
    // isNotify가 true인 경우 알림 생성
    if (Boolean.TRUE.equals(saveSchedule.getIsNotify())) {
      notificationService.createPersonalScheduleNotification(saveSchedule);
    }
    return saveSchedule.getIdx();
  }

  public List<PersonalScheduleResp> getSchedules(LocalDateTime date, User user) {
    LocalDateTime startOfMonth = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endOfMonth = date.withDayOfMonth(date.toLocalDate().lengthOfMonth())
        .withHour(23).withMinute(59).withSecond(59);
    return personalScheduleRepository.findPersonalSchedulesBetween(user.getIdx(), startOfMonth, endOfMonth);
  }
}