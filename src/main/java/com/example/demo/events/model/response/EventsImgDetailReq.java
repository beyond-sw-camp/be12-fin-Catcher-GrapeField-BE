package com.example.demo.events.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 상세 정보 이미지 응답")
public class EventsImgDetailReq {
  @Schema(description = "공연/전시 소개 이미지 URL", example = "/sample/images/poster/poster1.jpg")
  private String imgUrl;
  @Schema(description = "이미지 나열 순서, 숫자가 작을수록 상단에 먼저 표시", example = "1")
  private Long displayOrder;
}
