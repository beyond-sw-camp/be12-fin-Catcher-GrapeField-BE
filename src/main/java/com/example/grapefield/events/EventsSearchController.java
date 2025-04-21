package com.example.grapefield.events;

import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.events.post.model.response.PostSearchListResp;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.events.review.model.response.ReviewSearchList;
import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class EventsSearchController {
  private final EventsSearchService searchService;

  //Header를 이용한 통합 검색(검색 결과를 Map형식으로 한꺼번에 반환)
  @GetMapping("/all")
  public ResponseEntity<Map<String,Object>> searchAll(@RequestParam String keyword, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;

    Map<String, Object> result = new HashMap<>();
    result.put("events", searchService.searchEvents(keyword, user));
    result.put("posts", searchService.searchPosts(keyword, user));
    result.put("reviews", searchService.searchReviews(keyword, user));
    return ResponseEntity.ok(result);
  }

  // 공연/전시 상세 검색(더보기)
  @GetMapping("/events")
  public ResponseEntity<Page<EventsListResp>> searchEvents(@RequestParam String keyword, @PageableDefault Pageable pageable, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<EventsListResp> result = searchService.searchEvents(keyword, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 공연/전시 결과 내 재검색
  @GetMapping("/events/refine")
  public ResponseEntity<Page<EventsListResp>> searchEventsRefine( @RequestParam List<String> keywords, @PageableDefault(size = 30) Pageable pageable, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<EventsListResp> result = searchService.searchEventsRefine(keywords, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 게시글 검색(더보기)
  @GetMapping("/posts")
  public ResponseEntity<Page<PostSearchListResp>> searchPosts(@RequestParam String keyword, @PageableDefault Pageable pageable, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<PostSearchListResp> result = searchService.searchPosts(keyword, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 게시글 결과 내 재검색
  @GetMapping("/posts/refine")
  public ResponseEntity<Page<PostSearchListResp>> searchPostsRefine( @RequestParam List<String> keywords, @PageableDefault(size = 30) Pageable pageable, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<PostSearchListResp> result = searchService.searchPostsRefine(keywords, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 후기 상세 검색(더보기)
  @GetMapping("/reviews")
  public ResponseEntity<Page<ReviewSearchList>> searchReviews(@RequestParam String keyword, @PageableDefault Pageable pageable, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<ReviewSearchList> result = searchService.searchReviews(keyword, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 후기 결과 내 재검색
  @GetMapping("/reviews/refine")
  public ResponseEntity<Page<ReviewSearchList>> searchReviewsRefine(@RequestParam List<String> keywords, @PageableDefault(size = 30) Pageable pageable, @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<ReviewSearchList> result = searchService.searchReviewsRefine(keywords, pageable, user);
    return ResponseEntity.ok(result);
  }
}
