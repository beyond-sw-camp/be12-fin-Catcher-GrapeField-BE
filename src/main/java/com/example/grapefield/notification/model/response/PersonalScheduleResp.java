package com.example.grapefield.notification.model.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.grapefield.notification.model.entity.PersonalSchedule;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalScheduleResp {
  private Long idx;
  private String title;
  private String description;
  private LocalDateTime startDate;
  private Boolean isNotify;

  public static PersonalScheduleResp fromEntity(PersonalSchedule schedule) {
    return PersonalScheduleResp.builder()
        .idx(schedule.getIdx())
        .title(schedule.getTitle())
        .description(schedule.getDescription())
        .startDate(schedule.getStartDate())
        .isNotify(schedule.getIsNotify())
        .build();
  }
}
