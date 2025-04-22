package com.example.grapefield.notification.service;

import com.example.grapefield.notification.model.response.EventInterestCalendarResp;
import com.example.grapefield.notification.reposistory.EventsInterestRepository;
import com.example.grapefield.notification.reposistory.PersonalScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarFacadeService {
  private final PersonalScheduleRepository personalScheduleRepository;
  private final EventsInterestRepository eventsInterestRepository;

  public List<EventInterestCalendarResp> getMyInterestedEvents(Long userIdx, LocalDateTime date) {
    LocalDateTime startOfMonth = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endOfMonth = date.withDayOfMonth(date.toLocalDate().lengthOfMonth())
        .withHour(23).withMinute(59).withSecond(59);

    return eventsInterestRepository.findMyInterestedEventsBetween(userIdx, startOfMonth, endOfMonth);
  }

}
