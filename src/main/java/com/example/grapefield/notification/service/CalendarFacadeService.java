package com.example.grapefield.notification.service;

import com.example.grapefield.notification.model.response.EventInterestCalendarResp;
import com.example.grapefield.notification.model.response.PersonalScheduleResp;
import com.example.grapefield.notification.reposistory.EventsInterestRepository;
import com.example.grapefield.notification.reposistory.PersonalScheduleRepository;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CalendarFacadeService {
  private final PersonalScheduleRepository personalScheduleRepository;
  private final EventsInterestRepository eventsInterestRepository;


  public Map<String, Object> getAllCalendar(LocalDateTime date, Long userIdx) {
    LocalDateTime startOfMonth = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endOfMonth = date.withDayOfMonth(date.toLocalDate().lengthOfMonth())
            .withHour(23).withMinute(59).withSecond(59);
    List<PersonalScheduleResp> personal = personalScheduleRepository.findPersonalSchedulesBetween(userIdx, startOfMonth, endOfMonth);
    List<EventInterestCalendarResp> event = eventsInterestRepository.findMyInterestedEventsBetween(userIdx, startOfMonth, endOfMonth);

    Map<String, Object> result = new HashMap<>();
    result.put("personal", personal);
    result.put("event", event);
    return result;
  }


  public List<EventInterestCalendarResp> getMyInterestedEvents(Long userIdx, LocalDateTime date) {
    LocalDateTime startOfMonth = date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endOfMonth = date.withDayOfMonth(date.toLocalDate().lengthOfMonth())
        .withHour(23).withMinute(59).withSecond(59);

    return eventsInterestRepository.findMyInterestedEventsBetween(userIdx, startOfMonth, endOfMonth);
  }



}
