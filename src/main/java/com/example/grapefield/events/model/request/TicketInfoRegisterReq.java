package com.example.grapefield.events.model.request;

import com.example.grapefield.events.model.entity.TicketVendor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시의 티켓 정보")
public class TicketInfoRegisterReq {
  @Schema(description="예매처 링크", example="https://tickets.interpark.com/goods/24016737")
  private String ticketLink;
  @Schema(description = "선예매 여부", example = "true", allowableValues = {"true", "false"})
  private Boolean isPresale;
  @Schema(description="티켓 판매 시작일",  example = "2024-12-30T00:00:00")
  private LocalDateTime saleStart;
  @Schema(description="티켓 판매 종료일",  example = "2025-03-09T00:00:00")
  private LocalDateTime saleEnd;
  @Schema(description="예메처 정보", example="인터파크", allowableValues = {"인터파크", "예스24", "멜론티켓", "티켓링크", "티켓베이"})
  private TicketVendor ticketVendor;
}
