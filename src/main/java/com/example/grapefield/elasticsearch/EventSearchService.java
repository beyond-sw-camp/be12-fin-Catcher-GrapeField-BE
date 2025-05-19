package com.example.grapefield.elasticsearch;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.repository.EventsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventSearchService {

    private final co.elastic.clients.elasticsearch.ElasticsearchClient client;
    private final RestHighLevelClient restHighLevelClient;
    private final EventSearchRepository searchRepository;
    private final EventDocumentMapper documentMapper;
    private final ElasticsearchOperations elasticsearchOperations;
    private final EventsRepository eventsRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventSearchService(
            EventSearchRepository searchRepository,
            EventDocumentMapper documentMapper,
            co.elastic.clients.elasticsearch.ElasticsearchClient client,
            RestHighLevelClient restHighLevelClient,
            ElasticsearchOperations elasticsearchOperations,
            EventsRepository eventsRepository,
            ObjectMapper objectMapper) {
        this.searchRepository = searchRepository;
        this.documentMapper = documentMapper;
        this.client = client;
        this.restHighLevelClient = restHighLevelClient;
        this.elasticsearchOperations = elasticsearchOperations;
        this.eventsRepository = eventsRepository;
        this.objectMapper = objectMapper;
    }

    // 키워드로 검색 - 초성 검색과 짧은 단어 정확 매칭 개선
    public List<Events> searchByKeywordWithNori(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            System.out.println("Searching with Nori analyzer for keyword: " + keyword);

            // 쿼리 JSON 생성
            Map<String, Object> queryJson = new HashMap<>();
            queryJson.put("from", pageable.getOffset());
            queryJson.put("size", pageable.getPageSize());

            // 중복 제거
            Map<String, Object> collapse = new HashMap<>();
            collapse.put("field", "idx");
            queryJson.put("collapse", collapse);

            // 쿼리 부분 생성
            Map<String, Object> boolQuery = new HashMap<>();
            List<Map<String, Object>> shouldClauses = new ArrayList<>();

            // 제목 필드 검색 (높은 가중치)
            Map<String, Object> titleMatch = new HashMap<>();
            Map<String, Object> titleMatchParams = new HashMap<>();
            titleMatchParams.put("query", keyword);
            titleMatchParams.put("boost", 10.0);
            titleMatchParams.put("analyzer", "nori_analyzer");
            Map<String, Object> titleMatchField = new HashMap<>();
            titleMatchField.put("title", titleMatchParams);
            titleMatch.put("match", titleMatchField);
            shouldClauses.add(titleMatch);

            // 정확한 매칭에 더 높은 가중치
            Map<String, Object> keywordMatch = new HashMap<>();
            Map<String, Object> keywordMatchParams = new HashMap<>();
            keywordMatchParams.put("query", keyword);
            keywordMatchParams.put("boost", 50.0);
            Map<String, Object> keywordMatchField = new HashMap<>();
            keywordMatchField.put("title.keyword", keywordMatchParams);
            keywordMatch.put("match", keywordMatchField);
            shouldClauses.add(keywordMatch);

          // 초성 검색 추가
          Map<String, Object> chosungMatch = new HashMap<>();
          Map<String, Object> chosungMatchParams = new HashMap<>();
          chosungMatchParams.put("query", keyword);
          chosungMatchParams.put("boost", 5.0);
          Map<String, Object> chosungMatchField = new HashMap<>();
          chosungMatchField.put("title.chosung", chosungMatchParams);
          chosungMatch.put("match", chosungMatchField);
          shouldClauses.add(chosungMatch);

          // Seunjeon 분석기를 통한 검색 추가
          Map<String, Object> seunjeonMatch = new HashMap<>();
          Map<String, Object> seunjeonMatchParams = new HashMap<>();
          seunjeonMatchParams.put("query", keyword);
          seunjeonMatchParams.put("boost", 8.0);
          Map<String, Object> seunjeonMatchField = new HashMap<>();
          seunjeonMatchField.put("title.seunjeon", seunjeonMatchParams);
          seunjeonMatch.put("match", seunjeonMatchField);
          shouldClauses.add(seunjeonMatch);

          // 짧은 단어 정확 매칭을 위한 match_phrase 추가
          if (keyword.length() <= 3) {
            Map<String, Object> exactMatch = new HashMap<>();
            Map<String, Object> exactMatchParams = new HashMap<>();
            exactMatchParams.put("query", keyword);
            exactMatchParams.put("boost", 100.0);
            Map<String, Object> exactMatchField = new HashMap<>();
            exactMatchField.put("title", exactMatchParams);
            exactMatch.put("match_phrase", exactMatchField);
            shouldClauses.add(exactMatch);
          }

          // 다른 필드 검색
            Map<String, Object> multiMatch = new HashMap<>();
            Map<String, Object> multiMatchParams = new HashMap<>();
            multiMatchParams.put("query", keyword);
            multiMatchParams.put("fields", Arrays.asList("postTitle", "postContent", "review"));
            multiMatchParams.put("analyzer", "nori_analyzer");
            multiMatch.put("multi_match", multiMatchParams);
            shouldClauses.add(multiMatch);

            // 쿼리 조합
            Map<String, Object> bool = new HashMap<>();
            bool.put("should", shouldClauses);
            bool.put("minimum_should_match", 1);
            boolQuery.put("bool", bool);
            queryJson.put("query", boolQuery);

            // 하이라이트 설정
            Map<String, Object> highlight = new HashMap<>();
            Map<String, Object> titleHighlight = new HashMap<>();
            titleHighlight.put("pre_tags", Collections.singletonList("<em>"));
            titleHighlight.put("post_tags", Collections.singletonList("</em>"));
            titleHighlight.put("fragment_size", 150);
            Map<String, Object> fields = new HashMap<>();
            fields.put("title", titleHighlight);
            highlight.put("fields", fields);
            queryJson.put("highlight", highlight);

            // JSON 쿼리 생성 및 요청 설정
          String jsonQuery = objectMapper.writeValueAsString(queryJson);
          Request request = new Request("POST", "/events/_search");
          request.setJsonEntity(jsonQuery);

          System.out.println("Using index: [events]");
          System.out.println("Query: " + jsonQuery);

            // 요청 실행
            Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            // 응답 파싱
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> hits = (Map<String, Object>) responseMap.get("hits");
            List<Map<String, Object>> hitList = (List<Map<String, Object>>) hits.get("hits");

            System.out.println("Total hits: " + ((Map<String, Object>) hits.get("total")).get("value"));
            System.out.println("Response hits size: " + hitList.size());

            if (!hitList.isEmpty()) {
                Map<String, Object> firstHit = hitList.get(0);
                System.out.println("First hit ID: " + firstHit.get("_id"));
                System.out.println("First hit index: " + firstHit.get("_index"));

                Map<String, Object> source = (Map<String, Object>) firstHit.get("_source");
                if (source != null) {
                    System.out.println("First hit source: not null");
                    System.out.println("First hit title: " + source.get("title"));
                    System.out.println("First hit idx: " + source.get("idx"));
                } else {
                    System.out.println("First hit source: null");
                }
            }

            // ID 추출
            List<Long> eventIds = new ArrayList<>();
            for (Map<String, Object> hit : hitList) {
                Map<String, Object> source = (Map<String, Object>) hit.get("_source");
                if (source != null && source.containsKey("idx")) {
                    Object idxObj = source.get("idx");
                    if (idxObj instanceof Integer) {
                        eventIds.add(((Integer) idxObj).longValue());
                    } else if (idxObj instanceof Long) {
                        eventIds.add((Long) idxObj);
                    } else if (idxObj instanceof String) {
                        try {
                            eventIds.add(Long.parseLong((String) idxObj));
                        } catch (NumberFormatException e) {
                            System.err.println("Failed to parse idx: " + idxObj);
                        }
                    } else if (idxObj instanceof Number) {
                        eventIds.add(((Number) idxObj).longValue());
                    }
                }
            }

            System.out.println("Event IDs from ES: " + eventIds);

            // 결과가 없으면 빈 목록 반환
            if (eventIds.isEmpty()) {
                return Collections.emptyList();
            }

            // DB에서 완전한 엔티티 조회
            List<Events> events = eventsRepository.findAllByIdxIn(eventIds);

            // 결과 정렬 (Elasticsearch 결과 순서 유지)
            Map<Long, Events> eventsMap = events.stream()
                    .collect(Collectors.toMap(Events::getIdx, e -> e));

            List<Events> sortedEvents = eventIds.stream()
                    .map(eventsMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return sortedEvents;

        } catch (Exception e) {
            System.err.println("Elasticsearch search error: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 키워드와 카테고리로 검색
    public List<Events> searchByKeywordAndCategory(String keyword, String category) {
        // 카테고리가 있으면 카테고리 필터링 추가
        List<EventDocument> documents;
        if (category != null && !category.isEmpty()) {
            documents = searchRepository.findByCategoryAndTitleContaining(category, keyword);
        } else {
            documents = searchRepository.findByTitleContaining(keyword);
        }

        return documents.stream()
                .map(documentMapper::toEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 페이징 처리된 검색
    public List<Events> searchWithPagination(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return searchByKeywordWithNori(keyword, pageable);
    }

    // 문서 수 반환
    public long countDocuments() {
        return searchRepository.count();
    }

    public List<EventDocument> getSampleDocuments(int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<EventDocument> results = new ArrayList<>();
        searchRepository.findAll(pageable).forEach(results::add);
        return results;
    }

    /**
     * MariaDB의 Events 엔티티를 Elasticsearch에 인덱싱
     * @param event 인덱싱할 이벤트 엔티티
     */
    public void indexEvent(Events event) {
        try {
            // 엔티티를 Elasticsearch 문서로 변환
            EventDocument document = documentMapper.toDocument(event);

            // Elasticsearch에 저장
            searchRepository.save(document);

            System.out.println("Event successfully indexed with ID: " + event.getIdx());
        } catch (Exception e) {
            System.err.println("Error indexing event: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to index event", e);
        }
    }

    /**
     * Elasticsearch에서 이벤트 문서 삭제
     * @param id 삭제할 이벤트 ID
     */
    public void deleteEventDocument(Long id) {
        try {
            // Elasticsearch에서 문서 삭제
            searchRepository.deleteById(id.toString());

            System.out.println("Event document successfully deleted with ID: " + id);
        } catch (Exception e) {
            System.err.println("Error deleting event document: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete event document", e);
        }
    }

    /**
     * 이벤트 문서 일괄 인덱싱 (초기 로드 또는 재색인용)
     * @param events 인덱싱할 이벤트 목록
     * @return 인덱싱된 문서 수
     */
    public int bulkIndexEvents(List<Events> events) {
        try {
            List<EventDocument> documents = events.stream()
                    .map(documentMapper::toDocument)
                    .collect(Collectors.toList());

            searchRepository.saveAll(documents);

            return documents.size();
        } catch (Exception e) {
            System.err.println("Error bulk indexing events: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to bulk index events", e);
        }
    }

    /**
     * Elasticsearch 인덱스 재구축 (전체 데이터 재인덱싱)
     * @param allEvents 모든 이벤트 목록
     * @return 인덱싱된 문서 수
     */
    public int rebuildIndex(List<Events> allEvents) {
        try {
            // 인덱스 초기화 (기존 문서 모두 삭제)
            searchRepository.deleteAll();

            // 모든 문서 재인덱싱
            return bulkIndexEvents(allEvents);
        } catch (Exception e) {
            System.err.println("Error rebuilding index: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to rebuild index", e);
        }
    }
}