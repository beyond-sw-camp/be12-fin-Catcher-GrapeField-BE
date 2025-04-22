package com.example.grapefield.notification.model.response;

import java.time.LocalDate;

import com.example.grapefield.notification.model.entity.PersonalSchedule;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalScheduleSummaryResp {
  private Long idx;
  private String title;
  private String description;
  private LocalDate startDate;

  public static PersonalScheduleSummaryResp fromEntity(PersonalSchedule schedule) {
    return PersonalScheduleSummaryResp.builder()
        .idx(schedule.getIdx())
        .title(schedule.getTitle())
        .description(schedule.getDescription())
        .startDate(schedule.getStartDate())
        .build();
  }
}
