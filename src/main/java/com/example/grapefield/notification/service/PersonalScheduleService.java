package com.example.grapefield.notification.service;

import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.request.PersonalScheduleReq;
import com.example.grapefield.notification.reposistory.PersonalScheduleRepository;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PersonalScheduleService {
  private final PersonalScheduleRepository personalScheduleRepository;
  public Long registerSchedule(PersonalScheduleReq request, User user) {
    PersonalSchedule schedule = PersonalSchedule.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .isNotify(request.getIsNotify())
        .startDate(request.getStartDate())
        .createdAt(LocalDate.now())
        .updatedAt(LocalDate.now())
        .build();
    PersonalSchedule saveSchedule = personalScheduleRepository.save(schedule);
    return saveSchedule.getIdx();
  }
}