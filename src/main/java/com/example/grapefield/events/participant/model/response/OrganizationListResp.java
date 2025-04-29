package com.example.grapefield.events.participant.model.response;

import com.example.grapefield.events.participant.model.entity.AssociationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 제작, 후원 기업 목록 정보 응답")
@Builder
public class OrganizationListResp {
  @Schema(example = "1")
  private Long idx;
  @Schema(description="제작, 후원 기업 이름", example = "(주)EMK뮤지컬컴퍼니")
  private String name;
  @Schema(description="기업 사진 or 로고")
  private String imgUrl;
  @Schema(description="연관 타입", example = "주최")
  private AssociationType type;
}
