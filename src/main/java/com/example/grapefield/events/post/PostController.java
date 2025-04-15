package com.example.grapefield.events.post;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.common.PageResponse;
import com.example.grapefield.events.post.model.entity.Post;
import com.example.grapefield.events.post.model.entity.PostType;
import com.example.grapefield.events.post.model.request.PostRegisterReq;
import com.example.grapefield.events.post.model.response.PostDetailResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
@Tag(name="4. 게시판 기능 ", description = "각 공연/전시마다 사람들이 의견을 나누고 정보를 공유할 수 있는 게시판")
public class PostController {
  private final PostService postService;

  @Operation(summary="게시글 등록", description = "공연 및 전시 게시판에서 게시글을 등록")
  @ApiResponses(
      @ApiResponse(responseCode = "200", description = "게시글 등록 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "게시글을 성공적으로 등록"))))
  @ApiErrorResponses
  @PostMapping("/register")
  public ResponseEntity<Long> postRegister(
      @RequestBody PostRegisterReq request, @AuthenticationPrincipal User user) {
    //TODO: 게시글 idx를 반환하여 등록된 게시글로 페이지 이동되도록 추후 수정
    return ResponseEntity.ok(1L);
  }

  @Operation(summary = "게시글 목록 조회", description = "게시판에 등록된 게시글을 리스트 형식으로 반환하여 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/list/{board_idx}")
  public ResponseEntity<PageResponse<PostListResp>> getPostList(
      @AuthenticationPrincipal User user,
      @PathVariable("board_idx") Long boardIdx,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<PostListResp> postPage = postService.getPostList(user, boardIdx, pageable);
    PageResponse<PostListResp> response = PageResponse.from(postPage, postPage.getContent());
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "게시글 상세 확인", description = "게시판에 등록된 게시글을 상세하게 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/{postIdx}")
  public ResponseEntity<PostDetailResp> getPostDetail(@PathVariable Long postIdx, @AuthenticationPrincipal User user
  ) {
    PostDetailResp dummy = new PostDetailResp();
    return ResponseEntity.ok().body(dummy);
  }

  @Operation(summary = "게시글 내용 수정", description = "기존에 게시한 글을 수정(작성자만 가능)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "게시글 수정 성공"))),
      @ApiResponse(responseCode = "403", description = "작성자가 아닌 경우 수정 불가",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "게시글 수정 권한이 없습니다.")))
  })
  @ApiErrorResponses
  @PutMapping("/update/{postIdx}")
  public ResponseEntity<String> updateComment(@PathVariable Long postIdx, @RequestBody PostRegisterReq request, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok("게시글 수정 성공");
  }

  @Operation(summary = "게시글 삭제", description = "기존에 게시한 글을 삭제(작성자 혹은 관리자만 가능, 실제로 DB상에서는 삭제하지 않고 is_visible을 false로 바꿈)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "게시글 삭제 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "게시글 삭제 성공"))),
      @ApiResponse(responseCode = "403", description = "작성자가 아닌 경우 삭제 불가",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "게시글 삭제 권한이 없습니다.")))
  })
  @ApiErrorResponses
  @PutMapping("/delete/{postIdx}")
  public ResponseEntity<String> updateComment(@PathVariable Long postIdx, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok("게시글 삭제 성공");
  }
  
  //TODO : 게시글 상단 고정(최대 5개)

  //TODO : 상단에 고정된 게시글 목록 불러오기
}