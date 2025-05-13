package com.example.grapefield.events;

import com.example.grapefield.elasticsearch.EventDocument;
import com.example.grapefield.elasticsearch.EventDocumentMapper;
import com.example.grapefield.elasticsearch.EventSearchRepository;
import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.response.EventsListResp;
import com.example.grapefield.events.post.model.response.PostListResp;
import com.example.grapefield.events.post.model.response.PostSearchListResp;
import com.example.grapefield.events.post.repository.PostRepository;
import com.example.grapefield.events.repository.EventsRepository;
import com.example.grapefield.events.review.model.response.ReviewListResp;
import com.example.grapefield.events.review.model.response.ReviewSearchList;
import com.example.grapefield.events.review.repository.ReviewRepository;
import com.example.grapefield.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventsSearchService {
  private final EventsRepository eventsRepository;
  private final PostRepository postRepository;
  private final ReviewRepository reviewRepository;

  private final EventSearchRepository eventSearchRepository;
  private EventDocumentMapper eventDocumentMapper = new EventDocumentMapper();
  private final ElasticsearchOperations elasticsearchOperations;

  public List<EventsListResp> searchEvents(String keyword, User user) {
    Pageable top4 = PageRequest.of(0, 6);
    return eventsRepository.findEventsByKeyword(keyword, top4, user).getContent();
  }

  public Page<EventsListResp> searchEvents(String keyword, Pageable pageable, User user) {
    return eventsRepository.findEventsByKeyword(keyword, pageable, user);
  }

  public Page<EventsListResp> searchEventsRefine(List<String> keywords, Pageable pageable, User user) {
    return eventsRepository.findEventsByKeywordAnd(keywords, pageable, user);
  }

  public List<PostSearchListResp> searchPosts(String keyword, User user) {
    Pageable top10 = PageRequest.of(0, 10);
    return postRepository.findPostsByKeyword(keyword, top10, user).getContent();
  }

  public Page<PostSearchListResp> searchPosts(String keyword, Pageable pageable, User user) {
    return postRepository.findPostsByKeyword(keyword, pageable, user);
  }

  public Page<PostSearchListResp> searchPostsRefine(List<String> keywords, Pageable pageable, User user) {
    return postRepository.findPostsByKeywordAnd(keywords, pageable, user);
  }

  public List<ReviewSearchList> searchReviews(String keyword, User user) {
    Pageable top10 = PageRequest.of(0, 10);
    return reviewRepository.findReviewsByKeyword(keyword, top10, user).getContent();
  }

  public Page<ReviewSearchList> searchReviews(String keyword, Pageable pageable, User user) {
    return reviewRepository.findReviewsByKeyword(keyword, pageable, user);
  }

  public Page<ReviewSearchList> searchReviewsRefine(List<String> keywords, Pageable pageable, User user) {
    return reviewRepository.findReviewsByKeywordAnd(keywords, pageable, user);
  }

  /**
   * Elasticsearch를 사용한 고급 검색 기능 (초성 검색 포함)
   * @param keyword 검색어 (일반 키워드 또는 초성)
   * @return 이벤트 목록
   */
  public List<Events> searchEventsWithElasticsearch(String keyword) {
    Pageable top6 = PageRequest.of(0, 6);
    return searchEventsWithElasticsearch(keyword, top6);
  }

  /**
   * Elasticsearch를 사용한 고급 검색 (초성 검색 포함)
   * 페이징 처리된 결과
   */
  public List<Events> searchEventsWithElasticsearch(String keyword, Pageable pageable) {
    System.out.println("Elasticsearch search for keyword: " + keyword);

    try {
      // Elasticsearch에서 검색 수행
      Query query = new CriteriaQuery(
              new Criteria("title").contains(keyword)
                      .or("postTitle").contains(keyword)
                      .or("postContent").contains(keyword)
      );

      System.out.println("Elasticsearch query: " + query.toString());

      query.setPageable(pageable);
      SearchHits<EventDocument> searchHits = elasticsearchOperations.search(query, EventDocument.class);

      System.out.println("Total hits: " + searchHits.getTotalHits());

      // 검색 결과 확인
      for (SearchHit<EventDocument> hit : searchHits) {
        EventDocument doc = hit.getContent();
        System.out.println("Hit: id=" + doc.getIdx() + ", title=" + doc.getTitle());
      }

      // 검색 결과에서 ID 추출
      List<Long> eventIds = searchHits.stream()
              .map(hit -> {
                try {
                  return Long.parseLong(hit.getContent().getIdx());
                } catch (Exception e) {
                  System.err.println("Error parsing ID: " + hit.getContent().getIdx());
                  return null;
                }
              })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      System.out.println("Extracted " + eventIds.size() + " valid IDs");

      // ID 목록이 비어있으면 빈 목록 반환
      if (eventIds.isEmpty()) {
        System.out.println("No valid IDs found, returning empty list");
        return new ArrayList<>();
      }

      // 한 번의 쿼리로 모든 이벤트 조회
      List<Events> events = eventsRepository.findAllById(eventIds);
      System.out.println("Found " + events.size() + " events from database");

      // 검색 결과 순서에 맞게 정렬
      Map<Long, Events> eventsMap = events.stream()
              .collect(Collectors.toMap(Events::getIdx, Function.identity(), (e1, e2) -> e1));

      // 순서가 유지된 결과 목록 생성
      List<Events> result = eventIds.stream()
              .map(id -> {
                Events event = eventsMap.get(id);
                if (event == null) {
                  System.err.println("No matching event found for ID: " + id);
                }
                return event;
              })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      System.out.println("Final result size: " + result.size());
      return result;
    } catch (Exception e) {
      System.err.println("Error in searchEventsWithElasticsearch: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
  /**
   * Elasticsearch 검색 결과를 EventsListResp로 변환
   */
  public List<EventsListResp> searchEventsAdvanced(String keyword, User user) {
    Pageable top6 = PageRequest.of(0, 6);
    return searchEventsAdvanced(keyword, top6, user).getContent();
  }

  /**
   * Elasticsearch 검색 결과를 EventsListResp로 변환 (페이징)
   */
//  public Page<EventsListResp> searchEventsAdvanced(String keyword, Pageable pageable, User user) {
//    // Elasticsearch에서 검색
//    List<Events> events = searchEventsWithElasticsearch(keyword, pageable);
//    System.out.println("keyword is this!!!!!!!!!!!! "+keyword);
//
//    if (events.isEmpty()) {
//      return new PageImpl<>(List.of(), pageable, 0);
//    }
//
//    // 검색된 이벤트를 기존 메소드로 응답 형식으로 변환
//    // 이 부분은 실제 EventsListResp를 생성하는 방식에 따라 수정 필요
//    List<EventsListResp> results = events.stream()
//            .map(event -> {
//              // 여기서는 간단히 0을 전달하지만, 실제로는 알맞은 즐겨찾기 수를 전달해야 함
//              return EventsListResp.from(event, 0L);
//            })
//            .collect(Collectors.toList());
//
//    // 총 개수는 Elasticsearch 검색 결과와 동일하게 설정
//    long totalCount = eventSearchRepository.count();
//
//    return new PageImpl<>(results, pageable, totalCount);
//  }
  public Page<EventsListResp> searchEventsAdvanced(String keyword, Pageable pageable, User user) {
    System.out.println("Advanced search for keyword: " + keyword);

    List<Events> events;

    // 초성 여부 확인
    if (isChosung(keyword)) {
      System.out.println("Detected chosung search");
      events = searchEventsByChosung(keyword, pageable);
    } else {
      System.out.println("Regular search");
      events = searchEventsWithElasticsearch(keyword, pageable);
    }

    System.out.println("Found " + events.size() + " events after search");

    if (events.isEmpty()) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    // 응답 변환
    List<EventsListResp> results = events.stream()
            .map(event -> {
              System.out.println("Converting event: " + event.getTitle() + " (ID: " + event.getIdx() + ")");
              return EventsListResp.from(event, 0L);
            })
            .collect(Collectors.toList());

    return new PageImpl<>(results, pageable, results.size());
  }

  // 초성 여부 확인 메서드
  private boolean isChosung(String text) {
    String chosungs = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
    for (char c : text.toCharArray()) {
      if (chosungs.indexOf(c) == -1) {
        return false;
      }
    }
    return !text.isEmpty();
  }

  public List<Events> searchEventsByChosung(String keyword, Pageable pageable) {
    System.out.println("Searching for chosung: " + keyword);

    // 모든 이벤트 검색 (캐싱 고려 필요)
    List<Events> allEvents = eventsRepository.findAll();
    System.out.println("Loaded " + allEvents.size() + " events for chosung filtering");

    // 초성 변환 함수
    Function<String, String> extractChosung = text -> {
      StringBuilder result = new StringBuilder();
      for (char ch : text.toCharArray()) {
        if (ch >= 0xAC00 && ch <= 0xD7A3) { // 한글 범위
          int chosungIndex = (ch - 0xAC00) / (21 * 28);
          char[] chosungs = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
          result.append(chosungs[chosungIndex]);
        } else {
          result.append(ch);
        }
      }
      return result.toString();
    };

    // 초성이 포함된 이벤트 필터링
    List<Events> filteredEvents = allEvents.stream()
            .filter(event -> {
              String titleChosung = extractChosung.apply(event.getTitle());
              boolean matches = titleChosung.contains(keyword);
              if (matches) {
                System.out.println("Chosung match: " + event.getTitle() + " -> " + titleChosung);
              }
              return matches;
            })
            .collect(Collectors.toList());

    System.out.println("Found " + filteredEvents.size() + " events with matching chosung");

    // 페이징 처리
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

    if (start >= filteredEvents.size()) {
      return new ArrayList<>();
    }

    return filteredEvents.subList(start, end);
  }

  // 즐겨찾기 수 계산 메소드
  private Long calculateFavoriteCount(Events event) {
    // 이벤트 즐겨찾기 수 계산 로직
    return 0L; // 간단히 0 반환
  }

  /**
   * 초성 검색 특화 메소드 (Elasticsearch 사용)
   */
  public Page<EventsListResp> searchEventsByInitials(String initials, Pageable pageable, User user) {
    // 초성 검색은 기본적으로 searchEventsAdvanced와 동일하게 동작
    return searchEventsAdvanced(initials, pageable, user);
  }

  /**
   * 이벤트 데이터를 Elasticsearch에 인덱싱
   */
  public void indexEvent(Events event) {
    EventDocument document = eventDocumentMapper.toDocument(event);
    eventSearchRepository.save(document);
  }

  /**
   * 여러 이벤트 데이터를 Elasticsearch에 인덱싱
   */
  public void indexEvents(List<Events> events) {
    List<EventDocument> documents = events.stream()
            .map(eventDocumentMapper::toDocument)
            .collect(Collectors.toList());
    eventSearchRepository.saveAll(documents);
  }

  /**
   * Elasticsearch 인덱스에서 이벤트 문서 삭제
   */
  public void deleteEventDocument(Long eventId) {
    eventSearchRepository.deleteById(eventId.toString());
  }
}
