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
public class EventsCalendarListResp {
    @Schema(example = "1")
    private Long idx;
    @Schema(description="공연 제목", example = "웃는 남자")
    private String title;
    @Schema(example="뮤지컬")
    private EventCategory category;
    @Schema(description="예매 시작일",  example = "2024-12-30T00:00:00")
    private LocalDateTime saleStart;
    @Schema(description="예매 종료",  example = "2025-01-15T00:00:00")
    private LocalDateTime saleEnd;
    @Schema(description="예메처 정보", example="인터파크")
    private TicketVendor ticketVendor;
    @Schema(description = "선예매 여부", example = "true")
    private Boolean isPresale;
}
