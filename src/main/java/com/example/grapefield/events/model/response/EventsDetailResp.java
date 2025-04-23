package com.example.grapefield.events.model.response;

import com.example.grapefield.events.model.entity.AgeLimit;
import com.example.grapefield.events.model.entity.EventCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 상세 정보 응답")
@Builder
public class EventsDetailResp {
  private Long idx;
  @Schema(description="공연 제목", example = "웃는 남자")
  private String title;
  @Schema(description="공연/전시 카테고리", example="뮤지컬")
  private EventCategory category;
  @Schema(description="공연/전시 시작일",  example = "2025-01-09T00:00:00")
  private LocalDateTime startDate;
  @Schema(description="공연/전시 종료일", example = "2025-03-09T00:00:00")
  private LocalDateTime endDate;
  @Schema(description = "공연/전시 포스터 이미지 URL", example = "/sample/images/poster/poster1.jpg")
  private String posterImgUrl;
  @Schema(description = "공연/전시에 대한 소개 정보", example = "뮤지컬 웃는 남자입니다.")
  private String description;
  @Schema(description = "관람시 유의 사항", example = "※ 본 공연은 예매 티켓 배송 및 예술의전당 서비스플라자 방문구매가 불가하며 공연 당일 현장수령만 가능합니다.\n" +
      "※ 본 공연은 예매대기 서비스가 적용되지 않습니다.")
  private String notification;
  @Schema(description="공연/전시 위치", example = "예술의전당 오페라극장")
  private String venue;
  @Schema(description="상영 및 관람 시간, 분 단위로 표기", example = "120")
  private Integer runningTime;
  @Schema(description="관람 연령", example="12세 이상")
  private AgeLimit ageLimit;

  @Schema(description = "티켓 정보 목록")
  private List<TicketInfoDetailResp> ticketInfoList;

  @Schema(description = "좌석별 가격 정보 목록")
  private List<SeatPriceDetailResp> seatPriceList;

  //사용자 맞춤 정보(즐겨찾기 여부, 알림 여부)
  private Boolean isFavorite;
  private Boolean isNotify;

  //TODO : 한줄평 별점 합산
}
