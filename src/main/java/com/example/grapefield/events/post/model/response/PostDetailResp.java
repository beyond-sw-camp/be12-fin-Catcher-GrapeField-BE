package com.example.grapefield.events.post.model.response;

import com.example.grapefield.events.post.model.entity.PostType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="게시글 상세 내용 응답")
public class PostDetailResp {
  @Schema(example = "1")
  private Long idx;
  @Schema(description = "게시글 신고를 위해 유저 정보가 필요할 때 사용")
  private Long user_idx;
  @Schema(description="작성자", example = "김독자")
  private String writer;
  @Schema(description="게시글 제목", example = "웃는 남자 재미있었습니다.")
  private String title;
  @Schema(description="게시글 내용", example = "지난 주말에 보고 왔는데 배우들의 연기가 좋고 스토리도 좋았습니다.")
  private String content;
  @Schema(description="게시글 조회수", example="100")
  private Long viewCnt;
  @Schema(example="후기")
  private PostType postType;
  @Schema(description="게시글 등록일",  example = "2025-01-09T00:00:00")
  private LocalDateTime createdAt;
  @Schema(description="게시글 추천수", example="50")
  private int recommendCnt;
  @Schema(description="현재 로그인한 유저가 작성자이거나 ADMIN일 경우 게시글을 수정/삭제 가능하도록 프론트에 반환하기 위한 값", example = "false")
  private boolean editable;
}
