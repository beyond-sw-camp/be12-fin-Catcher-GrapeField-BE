package com.example.grapefield.events.post.model.response;

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
@Schema(description="마이페이지 내 시용자 작성 게시글 목록 정보 응답")
@Builder
public class UserReviewListResp {
    @Schema(description = "한줄평 번호")
    private Long idx;
    @Schema(description="한줄평 내용", example = "햄릿의 독백 장면이 압권이었습니다. 배우의 감정 표현이 정말 섬세했어요!")
    private String content;
    @Schema(description="게시글 등록일",  example = "2025-01-09T00:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "별점", example = "5")
    private Long rating;
    @Schema(description = "공연 번호", example = "1")
    private Long eventIdx;
    @Schema(description="공연 제목", example = "연극 <햄릿>")
    private String eventTitle;
    @Schema(description="공연 포스터", example = "/images/연극 <햄릿>/poster.jpg")
    private String poster_img_url;
    @Schema(description="카테고리", example = "PLAY")
    private EventCategory category;
}