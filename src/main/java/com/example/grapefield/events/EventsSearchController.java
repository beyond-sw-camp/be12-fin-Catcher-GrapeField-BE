package com.example.grapefield.events;

import com.example.grapefield.elasticsearch.EventSearchService;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.response.PostSearchListResp;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.events.review.model.response.ReviewSearchList;
import com.example.grapefield.user.CustomUserDetails;
import com.example.grapefield.user.model.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
@Tag(name = "검색 API", description = "통합 검색 및 엘라스틱서치 기반 고급 검색 API")
public class EventsSearchController {
  private final RestHighLevelClient restHighLevelClient;
  private final ObjectMapper objectMapper;
  private final EventsSearchService searchService;
  private final EventsRepository eventsRepository;
  @Lazy
  private final EventSearchService eventSearchService;

  //Header를 이용한 통합 검색(검색 결과를 Map형식으로 한꺼번에 반환)
  @Operation(summary = "통합 검색", description = "이벤트, 게시글, 후기를 한 번에 검색합니다")
  @GetMapping("/all")
  public ResponseEntity<Map<String,Object>> searchAll(
          @RequestParam String keyword,
          @AuthenticationPrincipal CustomUserDetails principal,
          @PageableDefault Pageable pageable) {
    User user = (principal != null) ? principal.getUser() : null;
    Map<String, Object> result = new HashMap<>();

    // ES 검색 결과 - ID만 추출
    List<Events> esResults = eventSearchService.searchByKeywordWithNori(keyword, pageable);
    List<Long> eventIds = esResults.stream()
            .map(Events::getIdx)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    System.out.println("Event IDs from ES: " + eventIds);

    // DB에서 완전한 Events 객체 조회
    if (!eventIds.isEmpty()) {
      // DB에서 정확한 데이터 조회
      List<Events> completedEvents = eventsRepository.findAllByIdxIn(eventIds);

      // ES 결과 순서 유지
      Map<Long, Events> eventsMap = completedEvents.stream()
              .collect(Collectors.toMap(Events::getIdx, e -> e));

      List<EventsListResp> sortedEvents = eventIds.stream()
              .map(id -> eventsMap.get(id))
              .filter(Objects::nonNull)
              .map(EventsListResp::from)  // 엔티티를 DTO로 변환
              .collect(Collectors.toList());

      result.put("events", sortedEvents);
    } else {
      result.put("events", Collections.emptyList());
    }

    result.put("posts", searchService.searchPosts(keyword, user));
    result.put("reviews", searchService.searchReviews(keyword, user));

    return ResponseEntity.ok(result);
  }

  @GetMapping("/autocomplete")
  public ResponseEntity<List<Map<String, Object>>> autocomplete(@RequestParam String prefix) {
    try {
      // 메서드 내에서 새로운 클라이언트를 생성하는 대신 주입된 클라이언트 사용
      // RestHighLevelClient client = new RestHighLevelClient(
      //         RestClient.builder(new HttpHost("localhost", 9200, "http")));

      // Nori 분석기를 사용하는 쿼리로 변경
      String query = "{\n" +
              "  \"size\": 5,\n" +
              "  \"collapse\": {\n" +
              "    \"field\": \"title.keyword\"\n" +
              "  },\n" +
              "  \"query\": {\n" +
              "    \"bool\": {\n" +
              "      \"should\": [\n" +
              "        { \"match\": { \"title\": { \"query\": \"" + prefix + "\", \"boost\": 3.0, \"analyzer\": \"nori_analyzer\" } } },\n" +
              "        { \"match_phrase_prefix\": { \"title\": { \"query\": \"" + prefix + "\", \"analyzer\": \"nori_analyzer\" } } }\n" +
              "      ]\n" +
              "    }\n" +
              "  }\n" +
              "}";

      Request request = new Request("GET", "/events/_search");
      request.setJsonEntity(query);

      // 주입된 클라이언트 사용
      Response response = this.restHighLevelClient.getLowLevelClient().performRequest(request);

      // 주입된 ObjectMapper 사용
      Map<String, Object> responseMap = this.objectMapper.readValue(
              EntityUtils.toString(response.getEntity()),
              new TypeReference<Map<String, Object>>() {});

      Map<String, Object> hits = (Map<String, Object>) responseMap.get("hits");
      List<Map<String, Object>> hitList = (List<Map<String, Object>>) hits.get("hits");

      // 스코어와 제목을 함께 반환
      List<Map<String, Object>> suggestions = hitList.stream()
              .map(hit -> {
                Map<String, Object> source = (Map<String, Object>) hit.get("_source");
                Map<String, Object> result = new HashMap<>();
                result.put("title", source.get("title"));
                result.put("score", hit.get("_score"));
                return result;
              })
              .collect(Collectors.toList());

      return ResponseEntity.ok(suggestions);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().body(List.of());
    }
  }

  // 공연/전시 상세 검색(더보기)
  @Operation(summary = "이벤트 검색", description = "DB 기반 일반 검색으로 이벤트를 검색합니다")
  @GetMapping("/events")
  public ResponseEntity<Page<EventsListResp>> searchEvents(
          @Parameter(description = "검색 키워드") @RequestParam String keyword,
          @PageableDefault Pageable pageable,
          @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<EventsListResp> result = searchService.searchEvents(keyword, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 공연/전시 결과 내 재검색
  @Operation(summary = "이벤트 결과 내 재검색", description = "이전 검색 결과 내에서 추가 키워드로 재검색합니다")
  @GetMapping("/events/refine")
  public ResponseEntity<Page<EventsListResp>> searchEventsRefine(
          @Parameter(description = "검색 키워드 목록") @RequestParam List<String> keywords,
          @PageableDefault(size = 30) Pageable pageable,
          @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<EventsListResp> result = searchService.searchEventsRefine(keywords, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 엘라스틱서치 기반 고급 이벤트 검색 (초성 검색 지원)
  @Operation(summary = "고급 이벤트 검색", description = "엘라스틱서치 기반 고급 검색 기능으로 이벤트를 검색합니다 (초성 검색 지원)")
  @GetMapping("/events/advanced")
  public ResponseEntity<Page<EventsListResp>> searchEventsAdvanced(
          @Parameter(description = "검색 키워드 (일반 텍스트 또는 초성)") @RequestParam String keyword,
          @PageableDefault Pageable pageable,
          @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<EventsListResp> result = searchService.searchEventsAdvanced(keyword, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 엘라스틱서치 기반 통합 검색 (초성 검색 지원)
  @Operation(summary = "고급 통합 검색", description = "엘라스틱서치 기반 고급 검색 기능으로 통합 검색을 수행합니다 (초성 검색 지원)")
  @GetMapping("/all/advanced")
  public ResponseEntity<Map<String,Object>> searchAllAdvanced(
          @Parameter(description = "검색 키워드 (일반 텍스트 또는 초성)") @RequestParam String keyword,
          @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;

    Map<String, Object> result = new HashMap<>();
    // 여기서는 이벤트만 고급 검색을 적용하고 나머지는 기존 검색 사용
    // 필요에 따라 다른 유형도 고급 검색으로 구현 가능
    result.put("events", searchService.searchEventsAdvanced(keyword, user));
    result.put("posts", searchService.searchPosts(keyword, user));
    result.put("reviews", searchService.searchReviews(keyword, user));
    return ResponseEntity.ok(result);
  }

  // 게시글 검색(더보기)
  @Operation(summary = "게시글 검색", description = "게시글을 검색합니다")
  @GetMapping("/posts")
  public ResponseEntity<Page<PostSearchListResp>> searchPosts(
          @Parameter(description = "검색 키워드") @RequestParam String keyword,
          @PageableDefault Pageable pageable,
          @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<PostSearchListResp> result = searchService.searchPosts(keyword, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 게시글 결과 내 재검색
  @Operation(summary = "게시글 결과 내 재검색", description = "이전 게시글 검색 결과 내에서 추가 키워드로 재검색합니다")
  @GetMapping("/posts/refine")
  public ResponseEntity<Page<PostSearchListResp>> searchPostsRefine(
          @Parameter(description = "검색 키워드 목록") @RequestParam List<String> keywords,
          @PageableDefault(size = 30) Pageable pageable,
          @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<PostSearchListResp> result = searchService.searchPostsRefine(keywords, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 후기 상세 검색(더보기)
  @Operation(summary = "후기 검색", description = "후기를 검색합니다")
  @GetMapping("/reviews")
  public ResponseEntity<Page<ReviewSearchList>> searchReviews(
          @Parameter(description = "검색 키워드") @RequestParam String keyword,
          @PageableDefault Pageable pageable,
          @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<ReviewSearchList> result = searchService.searchReviews(keyword, pageable, user);
    return ResponseEntity.ok(result);
  }

  // 후기 결과 내 재검색
  @Operation(summary = "후기 결과 내 재검색", description = "이전 후기 검색 결과 내에서 추가 키워드로 재검색합니다")
  @GetMapping("/reviews/refine")
  public ResponseEntity<Page<ReviewSearchList>> searchReviewsRefine(
          @Parameter(description = "검색 키워드 목록") @RequestParam List<String> keywords,
          @PageableDefault(size = 30) Pageable pageable,
          @AuthenticationPrincipal CustomUserDetails principal) {
    User user = (principal != null) ? principal.getUser() : null;
    Page<ReviewSearchList> result = searchService.searchReviewsRefine(keywords, pageable, user);
    return ResponseEntity.ok(result);
  }
}