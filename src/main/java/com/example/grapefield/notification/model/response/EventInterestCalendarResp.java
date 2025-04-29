package com.example.grapefield.notification.model.response;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.TicketVendor;
import com.example.grapefield.events.model.response.EventsDetailCalendarListResp;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventInterestCalendarResp extends EventsDetailCalendarListResp {

  private Long eventInterestIdx;
  private Boolean isNotify;

  public EventInterestCalendarResp(
      Long idx, String title, EventCategory category,
      LocalDateTime saleStart, LocalDateTime saleEnd,
      TicketVendor ticketVendor, Boolean isPresale,
      String venue, LocalDateTime startDate, LocalDateTime endDate,
      String ticketLink, Long eventInterestIdx, Boolean isNotify
  ) {
    super(idx, title, category, saleStart, saleEnd,
        ticketVendor, isPresale, venue, startDate, endDate, ticketLink);
    this.eventInterestIdx = eventInterestIdx;
    this.isNotify = isNotify;
  }
}
