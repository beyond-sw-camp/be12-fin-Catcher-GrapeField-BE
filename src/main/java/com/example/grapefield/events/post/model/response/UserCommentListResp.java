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
public class UserCommentListResp {
    @Schema(description = "댓글 번호")
    private Long idx;
    @Schema(description="댓글", example = "정말 재밌네요")
    private String comment;
    @Schema(description = "게시글 번호", example = "1")
    private Long postIdx;
    @Schema(description="게시글 제목", example = "웃는 남자 재미있었습니다.")
    private String postTitle;
    @Schema(description="게시글 등록일",  example = "2025-01-09T00:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "공연 번호", example = "1")
    private Long eventIdx;
    @Schema(description="공연 제목", example = "웃는 남자")
    private String eventTitle;
    @Schema(description="카테고리", example = "MUSICAL")
    private EventCategory category;
}