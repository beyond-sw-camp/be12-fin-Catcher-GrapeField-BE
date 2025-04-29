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
@Schema(description="전체 검색에서 게시글 목록 정보 응답")
@Builder
public class PostSearchListResp {
    @Schema(example = "1")
    private Long idx;
    @Schema(description="작성자", example = "김독자")
    private String writer;
    @Schema(description="게시글 제목", example = "웃는 남자 재미있었습니다.")
    private String title;
    @Schema(description="게시글 조회수", example="100")
    private int viewCnt;
    @Schema(example="후기")
    private PostType postType;
    @Schema(description="게시글 등록일",  example = "2025-01-09")
    private LocalDateTime createdAt;
    @Schema(description="게시글 표시 여부, 관리자는 true,false 상관 없이 전체 열람 가능, 일반 유저는 true만 열람 가능",  example = "true")
    private Boolean isVisible;
    private Long boardIdx;
    private String boardTitle;
}
