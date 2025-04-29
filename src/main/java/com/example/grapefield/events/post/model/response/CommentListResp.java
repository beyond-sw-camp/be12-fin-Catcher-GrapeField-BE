package com.example.grapefield.events.post.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="게시글 상세 페이지에서 댓글 목록 및 내용 응답")
public class CommentListResp {
  @Schema(example = "1")
  private Long idx;
  @Schema(description = "댓글 신고를 위해 유저 정보가 필요할 때 사용")
  private Long user_idx;
  @Schema(description="작성자", example = "이독자")
  private String writer;
  @Schema(description="댓글", example = "저도 무척이나 재밌게 보고 왔는데 공감되네요.")
  private String content;
  @Schema(description="댓글 등록일",  example = "2025-01-09T00:00:00")
  private LocalDateTime createdAt;
  @Schema(description="이 댓글이 수정된 것인지 여부", example = "true")
  private boolean edited;
  @Schema(description="현재 로그인한 유저가 작성자이거나 ADMIN일 경우 수정/삭제 가능하도록 프론트에 반환하기 위한 값", example = "false")
  private boolean editable;
}
