package com.example.grapefield.events.model.response;

import com.example.grapefield.events.model.entity.TicketVendor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 티켓 정보 응답")
public class TicketInfoDetailResp {
  @Schema(description="예매처 링크", example="https://tickets.interpark.com/goods/24016737")
  private String ticketLink;
  @Schema(description = "선예매 여부", example = "true")
  private Boolean isPresale;
  @Schema(description="티켓 판매 시작일",  example = "2024-12-30T00:00:00")
  private LocalDateTime saleStart;
  @Schema(description="티켓 판매 종료일",  example = "2025-03-09T00:00:00")
  private LocalDateTime saleEnd;
  @Schema(description="예메처 정보", example="인터파크")
  private TicketVendor ticketVendor;
}
