package com.example.demo.events.model.request;

import com.example.demo.events.model.entity.AgeLimit;
import com.example.demo.events.model.entity.EventCategory;
import com.example.demo.events.model.entity.Events;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="공연/전시 등록 body")
public class EventsRegisterReq {
  @NotBlank
  @Schema(description="공연 제목, 필수", example = "웃는 남자")
  @Pattern(regexp = "[0-9A-Za-z가-힣]+", message = "제목을 입력해주세요.")
  private String title;
  @NotBlank
  @NotNull(message = "카테고리를 선택해주세요.")
  @Schema(description="공연/전시 카테고리 선택", example="뮤지컬", allowableValues = {"뮤지컬", "콘서트", "연극", "전시회", "박람회"})
  private EventCategory category;
  @Schema(description="공연/전시 시작일",  example = "2025-01-09T00:00:00")
  private LocalDateTime startDate;
  @Schema(description="공연/전시 종료일", example = "2025-03-09T00:00:00")
  private LocalDateTime endDate;
  @Schema(description = "공연/전시 포스터 이미지 URL(문자열): 선택, 이미지 1장을 업로드하여 DB에 저장된 경로", example = "/sample/images/poster/poster1.jpg")
  private String posterImgUrl;
  @Schema(description = "공연/전시에 대한 소개 정보", example = "뮤지컬 웃는 남자입니다.")
  private String description;
  @Schema(description = "관람시 유의 사항", example = "※ 본 공연은 예매 티켓 배송 및 예술의전당 서비스플라자 방문구매가 불가하며 공연 당일 현장수령만 가능합니다.\n" +
      "※ 본 공연은 예매대기 서비스가 적용되지 않습니다.")
  private String notification;
  @Schema(description="공연/전시 위치", example = "예술의전당 오페라극장")
  private String venue;
  @Schema(description="상영 및 관람 시간, 분 단위로 표기", example = "120분")
  private Integer runningTime;
  @Schema(description="관람 연령", example="12세 이상", allowableValues = {"전체 관람가", "12세 이상", "15세 이상", "19세 이상"})
  private AgeLimit ageLimit;

  @Schema(description = "티켓 정보 목록")
  private List<TicketInfoRegisterReq> ticketInfoList;

  @Schema(description = "좌석별 가격 정보 목록")
  private List<SeatPriceRegisterReq> seatPriceList;

  @Schema(description = "추가 이미지 목록 (선택사항)")
  private List<EventsImgRegisterReq> eventsImgList;

  public Events toEntity() {
    return Events.builder()
        .title(title)
        .category(category)
        .startDate(startDate)
        .endDate(endDate)
        .posterImgUrl(posterImgUrl)
        .description(description)
        .notification(notification)
        .venue(venue)
        .runningTime(runningTime)
        .ageLimit(ageLimit)
        .build();
  }
}
