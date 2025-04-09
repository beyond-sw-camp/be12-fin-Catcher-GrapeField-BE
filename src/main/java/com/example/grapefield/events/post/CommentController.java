package com.example.grapefield.events.post;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.events.post.model.request.CommentRegisterReq;
import com.example.grapefield.events.post.model.response.CommentListResp;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
@Tag(name="5. 게시판 내의 댓글 기능 ", description = "게시글 내에서 댓글을 조회, 등록, 삭제하는 기능")
public class CommentController {
  @Operation(summary="댓글 등록", description = "게시글 상세 페이지에서 댓글을 등록")
  @ApiResponses(
      @ApiResponse(responseCode = "200", description = "댓글 등록 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "댓글을 성공적으로 등록"))))
  @ApiErrorResponses
  @PostMapping("/register")
  public ResponseEntity<Long> commentRegister(
      @RequestBody CommentRegisterReq request, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(1L);
  }

  @Operation(summary = "댓글 조회", description = "게시글에 달린 모든 댓글을 내용과 함께 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/{postIdx}")
  public ResponseEntity<List<CommentListResp>> getCommentList(@PathVariable Long postIdx, @AuthenticationPrincipal User user) {
    List<CommentListResp> dummyList = List.of(
        new CommentListResp(1L, 3L, "이독자", "저도 재미있게 보고 왔습니다.", LocalDateTime.now(), LocalDateTime.now(),false),
        new CommentListResp(1L, 3L, "곽독자", "예매를 놓쳤는데 다들 재밌다고 하니까 예매를 못한 게 무척 아쉽네요.", LocalDateTime.now(), LocalDateTime.now(),false)
    );
    return ResponseEntity.ok(dummyList);
  }

  @Operation(summary = "댓글 수정", description = "기존에 단 댓글을 수정(작성자만 가능)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "댓글 수정 성공"))),
      @ApiResponse(responseCode = "403", description = "작성자가 아닌 경우 수정 불가",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "댓글 수정 권한이 없습니다.")))
  })
  @ApiErrorResponses
  @PutMapping("/update/{commentIdx}")
  public ResponseEntity<String> updateComment(@PathVariable Long commentIdx, @RequestBody CommentRegisterReq request, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok("댓글 수정 성공");
  }

  @Operation(summary = "댓글 삭제", description = "기존에 달아둔 댓글을 삭제(작성자 혹은 관리자만 가능), 실제로 DB상에서는 삭제하지 않고, 삭제된 글이라고 content를 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "댓글 삭제 성공"))),
      @ApiResponse(responseCode = "403", description = "작성자가 아닌 경우 삭제 불가",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "댓글 삭제 권한이 없습니다.")))
  })
  @ApiErrorResponses
  @PutMapping("/delete/{commentIdx}")
  public ResponseEntity<String> deleteComment(@PathVariable Long commentIdx, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok("댓글 삭제 성공");
  }
}
