package com.example.grapefield.events.model.response;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.TicketVendor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 목록 정보 응답")
public class EventsDetailCalendarListResp extends EventsCalendarListResp{
    @Schema(description="공연 장소", example = "고척돔")
    private String venue;
    @Schema(description="공연 시작일",  example = "2024-12-30T00:00:00")
    private LocalDateTime startDate;
    @Schema(description="공연 종료일",  example = "2025-01-15T00:00:00")
    private LocalDateTime endDate;
    @Schema(description="예메처 링크", example="https://tickets.interpark.com/goods/25004056")
    private String ticketLink;

    public EventsDetailCalendarListResp(
            Long idx, String title, EventCategory category,
            LocalDateTime saleStart, LocalDateTime saleEnd,
            TicketVendor ticketVendor, Boolean isPresale,
            String venue, LocalDateTime startDate, LocalDateTime endDate,
            String ticketLink) {
        super(idx, title, category, saleStart, saleEnd, ticketVendor, isPresale);
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ticketLink = ticketLink;
    }
}
