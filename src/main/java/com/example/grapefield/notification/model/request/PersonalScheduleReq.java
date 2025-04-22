package com.example.grapefield.notification.model.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalScheduleReq {
  private Long userIdx;
  private String title;
  private String description;
  private LocalDateTime startDate;
  private Boolean isNotify;
}
