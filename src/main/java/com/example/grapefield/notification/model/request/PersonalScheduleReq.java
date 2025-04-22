package com.example.grapefield.notification.model.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalScheduleReq {
  private Long userIdx;
  private String title;
  private String description;
  private LocalDate startDate;
  private Boolean isNotify;
}
