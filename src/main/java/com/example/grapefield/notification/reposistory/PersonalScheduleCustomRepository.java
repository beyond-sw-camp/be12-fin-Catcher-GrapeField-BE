package com.example.grapefield.notification.reposistory;

import com.example.grapefield.notification.model.response.PersonalScheduleResp;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonalScheduleCustomRepository {
  List<PersonalScheduleResp> findPersonalSchedulesBetween(Long idx, LocalDateTime startOfMonth, LocalDateTime endOfMonth);
}
