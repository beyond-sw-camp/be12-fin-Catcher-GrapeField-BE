package com.example.grapefield.user.admin.model.response;

import com.example.grapefield.user.model.entity.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "신고된 게시글, 댓글, 한줄평, 채팅 목록 정보 응답")
public class ReportListResp {
  @Schema(description = "신고 Idx")
  private Long idx;
  @Schema(description = "신고 대상 타입 (POST, COMMENT 등)")
  private ReportTargetType type;
  @Schema(description = "신고 대상의 PK (예: 게시글 ID)")
  private Long item;
  @Schema(description = "신고 횟수")
  private int reportCnt;
  @Schema(description = "신고 사유")
  private String reason;
  @Schema(description = "신고 일시")
  private LocalDateTime createdAt;
  @Schema(description = "관리자 승인 여부")
  private Boolean isAccepted;
}
