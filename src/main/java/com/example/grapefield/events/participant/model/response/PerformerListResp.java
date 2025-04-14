package com.example.grapefield.events.participant.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 출연진 목록 정보 응답")
@Builder
public class PerformerListResp {
  @Schema(example = "1")
  private Long idx;
  @Schema(description="배우 이름", example = "김주연")
  private String name;
  @Schema(description="배우 프로필 사진")
  private String imgUrl;
  @Schema(description="역할", example = "햄릿")
  private String role;
}
