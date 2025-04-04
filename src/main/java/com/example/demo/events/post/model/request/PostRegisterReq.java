package com.example.demo.events.post.model.request;

import com.example.demo.events.post.model.entity.PostType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="게시글 등록 body")
public class PostRegisterReq {

  @NotBlank
  @Schema(description="게시글 제목, 필수", example = "웃는 남자 무척 재미있었습니다.")
  private String title;
  @NotBlank
  @Schema(description="게시글 내용, 필수", example = "지난 주말에 보고 왔는데 배우들의 연기가 좋고 스토리도 좋았습니다.")
  private String content;
  @Schema(description="공연/전시 카테고리 선택", example="후기", allowableValues = {"공지", "잡담", "정보", "후기", "질문"})
  private PostType postType;
}
