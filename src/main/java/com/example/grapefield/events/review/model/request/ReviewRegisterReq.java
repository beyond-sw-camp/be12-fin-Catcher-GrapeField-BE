package com.example.grapefield.events.review.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="한줄평 등록 body")
public class ReviewRegisterReq {
  @Schema(description="이벤트 idx")
  private Long eventIdx;
  @Schema(description="(선택)한줄평", example = "배우의 연기가 멋있었어요.")
  private String content;
  @Schema(description="별점")
  private Long rating;
}
