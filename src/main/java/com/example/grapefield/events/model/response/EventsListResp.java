package com.example.grapefield.events.model.response;

import com.example.grapefield.events.model.entity.EventCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 목록 정보 응답")
@Builder
public class EventsListResp {
  @Schema(example = "1")
  private Long idx;
  @Schema(description="공연 제목", example = "웃는 남자")
  private String title;
  @Schema(example="뮤지컬")
  private EventCategory category;
  @Schema(description="공연/전시 시작일",  example = "2025-01-09T00:00:00")
  private LocalDateTime startDate;
  @Schema(description="공연/전시 종료일", example = "2025-03-09T00:00:00")
  private LocalDateTime endDate;
  @Schema(description = "공연/전시 포스터 이미지 URL(문자열)", example = "/sample/images/poster/poster1.jpg")
  private String posterImgUrl;
  @Schema(description="공연/전시 위치", example = "예술의전당 오페라극장")
  private String venue;
  @Schema(description="즐겨찾기 수", example = "전시/공연의 유저가 즐겨찾기한 수")
  private int interestCtn;
}
