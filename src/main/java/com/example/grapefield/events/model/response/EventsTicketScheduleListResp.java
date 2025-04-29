package com.example.grapefield.events.model.response;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.TicketInfo;
import com.example.grapefield.events.model.entity.Events;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "예매 오픈/종료 예정 공연 응답")
public class EventsTicketScheduleListResp {
    @Schema(example = "1")
    private Long idx;
    @Schema(description = "공연 제목", example = "웃는 남자")
    private String title;
    @Schema(description = "카테고리", example = "뮤지컬")
    private EventCategory category;
    @Schema(description = "공연장", example = "예술의전당 오페라극장")
    private String venue;
    @Schema(description = "포스터 URL", example = "/sample/images/poster/poster1.jpg")
    private String posterImgUrl;
    @Schema(description = "티켓 예매 시작일", example = "2025-04-13T10:00:00")
    private LocalDateTime saleStart;
    @Schema(description = "티켓 예매 종료일", example = "2025-04-20T23:59:59")
    private LocalDateTime saleEnd;
    @Schema(description = "즐겨찾기 수", example = "12")
    private int interestCtn;

    public static EventsTicketScheduleListResp from(Events event, TicketInfo ticket, Long interestCtn) {
        return EventsTicketScheduleListResp.builder()
                .idx(event.getIdx())
                .title(event.getTitle())
                .category(event.getCategory())
                .venue(event.getVenue())
                .posterImgUrl(event.getPosterImgUrl())
                .saleStart(ticket.getSaleStart())
                .saleEnd(ticket.getSaleEnd())
                .interestCtn(interestCtn != null ? interestCtn.intValue() : 0)
                .build();
    }
}