package com.example.grapefield.events.review;

import com.example.grapefield.base.ApiErrorResponses;
import com.example.grapefield.base.ApiSuccessResponses;
import com.example.grapefield.common.PageResponse;
import com.example.grapefield.events.post.model.request.CommentRegisterReq;
import com.example.grapefield.events.post.model.response.CommentListResp;
import com.example.grapefield.events.review.model.request.ReviewRegisterReq;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/review")
public class ReviewController {
  private final ReviewService reviewService;
  //TODO : 한줄평 등록, 코멘트 없이 별점만 남기는 것도 가능
  @Operation(summary="한줄평 등록", description = "공연/전시 상세 페이지에서 한줄평을 등록")
  @ApiResponses(
      @ApiResponse(responseCode = "200", description = "등록 성공",
          content = @Content(mediaType = "text/plain",
              examples = @ExampleObject(value = "성공적으로 등록"))))
  @ApiErrorResponses
  @PostMapping("/register")
  public ResponseEntity<Long> registerReview(
      @RequestBody ReviewRegisterReq request, @AuthenticationPrincipal CustomUserDetails principal) {
    if (principal == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); }
    Long reviewIdx = reviewService.registerReview(request, principal.getUser());
    return ResponseEntity.ok(reviewIdx);
  }

  @Operation(summary = "한줄평 조회", description = "공연/행사에 달린 모든 한줄평을 내용과 함께 조회")
  @ApiSuccessResponses
  @ApiErrorResponses
  @GetMapping("/{idx}")
  public ResponseEntity<PageResponse<ReviewListResp>> getReviewList(@PathVariable Long idx, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(required = false) Long rating, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<ReviewListResp> reviewPage = reviewService.getReviewList(idx, pageable, rating, user);
    PageResponse<ReviewListResp> response = PageResponse.from(reviewPage, reviewPage.getContent());
    return ResponseEntity.ok(response);
  }

  //TODO:한줄평 삭제(실제로는 isVisible 을 false로 변환하는걸로 수정)
  
  //TODO: 한줄평 수정
}
