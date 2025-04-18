package com.example.grapefield.events;

import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Map<String,Object>> searchAll(@RequestParam String keyword) {
    Map<String, Object> result = new HashMap<>();
    result.put("events", searchService.searchEvents(keyword));
    result.put("posts", searchService.searchPosts(keyword));
    result.put("reviews", searchService.searchReviews(keyword));
    return ResponseEntity.ok(result);
  }

  // 공연/전시 상세 검색(더보기)
  @GetMapping("/events")
  public ResponseEntity<Slice<EventsListResp>> searchEvents(@RequestParam String keyword, @PageableDefault Pageable pageable) {
    Slice<EventsListResp> result = searchService.searchEvents(keyword, pageable);
    return ResponseEntity.ok(result);
  }

  // 공연/전시 결과 내 재검색
  @GetMapping("/events/refine")
  public ResponseEntity<Slice<EventsListResp>> searchEventsRefine( @RequestParam List<String> keywords, @PageableDefault(size = 30) Pageable pageable
  ) {
    Slice<EventsListResp> result = searchService.searchEventsRefine(keywords, pageable);
    return ResponseEntity.ok(result);
  }

  // 게시판 상세 검색(더보기)
  @GetMapping("/board")
  public ResponseEntity<Slice<PostListResp>> searchPosts(@RequestParam String keyword, @PageableDefault Pageable pageable) {
    Slice<PostListResp> result = searchService.searchPosts(keyword, pageable);
    return ResponseEntity.ok(result);
  }

  // 게시판 결과 내 재검색
  @GetMapping("/board/refine")
  public ResponseEntity<Slice<PostListResp>> searchPostsRefine( @RequestParam List<String> keywords, @PageableDefault(size = 30) Pageable pageable
  ) {
    Slice<PostListResp> result = searchService.searchPostsRefine(keywords, pageable);
    return ResponseEntity.ok(result);
  }

  // 후기 상세 검색(더보기)
  @GetMapping("/review")
  public ResponseEntity<Slice<ReviewListResp>> searchReviews(@RequestParam String keyword, @PageableDefault Pageable pageable) {
    Slice<ReviewListResp> result = searchService.searchReviews(keyword, pageable);
    return ResponseEntity.ok(result);
  }

  // 후기 결과 내 재검색
  @GetMapping("/review/refine")
  public ResponseEntity<Slice<ReviewListResp>> searchReviewsRefine( @RequestParam List<String> keywords, @PageableDefault(size = 30) Pageable pageable
  ) {
    Slice<ReviewListResp> result = searchService.searchReviewsRefine(keywords, pageable);
    return ResponseEntity.ok(result);
  }

}
