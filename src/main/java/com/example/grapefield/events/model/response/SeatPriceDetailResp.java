package com.example.grapefield.events.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 좌석별 가격 정보 응답")
public class SeatPriceDetailResp {
  @Schema(description="좌석 타입", example="VIP석")
  private String seatType;
  @Schema(description="좌석 가격", example="250,000원")
  private BigDecimal price;
  @Schema(description="좌석 정보", example="VVIP석 다음으로 무대와 가장 가까운 자리입니다.")
  private String description;
}
