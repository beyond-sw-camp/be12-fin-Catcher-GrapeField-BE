package com.example.grapefield.notification.model.response;

import java.time.LocalDateTime;

import com.example.grapefield.events.model.entity.Events;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSummaryResp {
  private Long idx;
  private String title;
  private String posterImgUrl;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String venue;

  public static EventSummaryResp fromEntity(Events event) {
    return EventSummaryResp.builder()
        .idx(event.getIdx())
        .title(event.getTitle())
        .posterImgUrl(event.getPosterImgUrl())
        .startDate(event.getStartDate())
        .endDate(event.getEndDate())
        .venue(event.getVenue())
        .build();
  }
}
