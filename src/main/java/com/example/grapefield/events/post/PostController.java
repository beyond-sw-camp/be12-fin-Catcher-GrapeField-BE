package com.example.grapefield.events.post;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.common.PageResponse;
import com.example.grapefield.events.post.model.request.PostRegisterReq;
import com.example.grapefield.events.post.model.request.PostUpdateReq;
import com.example.grapefield.events.post.model.response.CommunityPostListResp;
import com.example.grapefield.events.post.model.response.PostDetailResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
@Tag(name="4. 게시판 기능 ", description = "각 공연/전시마다 사람들이 의견을 나누고 정보를 공유할 수 있는 게시판")
public class PostController {
  private final PostService postService;
  private final HandlerMapping resourceHandlerMapping;

  @Operation(summary="게시글 등록", description = "공연 및 전시 게시판에서 게시글을 등록")
  @ApiResponses(
      @ApiResponse(responseCode = "200", description = "게시글 등록 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "게시글을 성공적으로 등록"))))
  @ApiErrorResponses
  @PostMapping("/register")
  public ResponseEntity<Long> registerPost(
      @RequestPart("request") @Valid PostRegisterReq request,
      @RequestPart(name = "images", required = false) MultipartFile[] images,
      @AuthenticationPrincipal CustomUserDetails principal
  ) {
    if (principal == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); }
    Long postIdx = postService.registerPost(request, images, principal.getUser());
    return ResponseEntity.ok(postIdx);
  }

  @Operation(summary = "인기, 최신 게시글 목록 조회", description = "커뮤니티에서 인기, 최신 게시글을 리스트 형식으로 반환하여 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/list")
  public ResponseEntity<PageResponse<CommunityPostListResp>> getPostList(
          @AuthenticationPrincipal CustomUserDetails principal,
          @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
          String type,
          String orderBy
  ) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<CommunityPostListResp> postPage = postService.getPostLists(user, pageable, type, orderBy);
    PageResponse<CommunityPostListResp> response = PageResponse.from(postPage, postPage.getContent());
    return ResponseEntity.ok(response);
  }


  @Operation(summary = "게시글 목록 조회", description = "게시판에 등록된 게시글을 리스트 형식으로 반환하여 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/list/{board_idx}")
  public ResponseEntity<PageResponse<PostListResp>> getPostList(
      @AuthenticationPrincipal CustomUserDetails principal,
      @PathVariable("board_idx") Long boardIdx,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, String type
  ) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<PostListResp> postPage = postService.getPostList(user, boardIdx, pageable, type);
    PageResponse<PostListResp> response = PageResponse.from(postPage, postPage.getContent());
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "게시글 상세 확인", description = "게시판에 등록된 게시글을 상세하게 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/{idx}")
  public ResponseEntity<PostDetailResp> getPostDetail(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    PostDetailResp post = postService.getPostDetail(idx, user);
    return ResponseEntity.ok(post);
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
  @PatchMapping("/edit/{postIdx}")
  public ResponseEntity<?> updatePost(
      @PathVariable Long postIdx,
      @RequestPart("request") @Valid PostUpdateReq request,
      @RequestPart(name = "images", required = false) MultipartFile[] images,
      @AuthenticationPrincipal CustomUserDetails principal
  ) {
    if (principal == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); }

    try {
      Long updatedPostIdx = postService.updatePost(postIdx, request, images, principal.getUser());
      return ResponseEntity.ok(updatedPostIdx);
    } catch (IllegalArgumentException e) {
      // 잘못된 요청 (존재하지 않는 게시글, 권한 없음 등)
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      // 서버 내부 오류
      System.out.println("서버 내부 오류: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
    }
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
  @PatchMapping("/delete/{postIdx}")
  public ResponseEntity<Boolean> deletePost(@PathVariable Long postIdx, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    boolean response = postService.deletePost(postIdx, user);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/view")
  public ResponseEntity<Integer> updateViewCount(@RequestParam Long postIdx){
    int viewCnt = postService.updateViewCount(postIdx);
    return ResponseEntity.ok(viewCnt);
  }
  
  //TODO : 게시글 상단 고정(최대 5개)

  //TODO : 상단에 고정된 게시글 목록 불러오기
}