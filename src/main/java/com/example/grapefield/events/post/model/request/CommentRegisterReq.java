package com.example.grapefield.events.post.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="댓글 등록 body")
public class CommentRegisterReq {
  @NotBlank
  @Schema(description="댓글 내용, 필수", example = "저도 무척이나 재미있게 보았는데 공감되네요.")
  private String content;
}
