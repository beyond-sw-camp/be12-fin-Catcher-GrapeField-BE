package com.example.grapefield.elasticsearch;

import com.example.grapefield.events.model.entity.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventSearchService {

    private final co.elastic.clients.elasticsearch.ElasticsearchClient client;
    private final EventSearchRepository searchRepository;
    private final EventDocumentMapper documentMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public EventSearchService(
            EventSearchRepository searchRepository,
            EventDocumentMapper documentMapper,
            co.elastic.clients.elasticsearch.ElasticsearchClient client,
            ElasticsearchOperations elasticsearchOperations) {
        this.searchRepository = searchRepository;
        this.documentMapper = documentMapper;
        this.client = client;
        this.elasticsearchOperations = elasticsearchOperations;
    }



    // 키워드로 검색 - 초성 검색과 짧은 단어 정확 매칭 개선
    public List<Events> searchByKeywordWithNori(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            System.out.println("Searching with Nori analyzer for keyword: " + keyword);

            // Nori 분석기를 활용한 고급 검색 쿼리
            var searchRequest = new co.elastic.clients.elasticsearch.core.SearchRequest.Builder()
                    .index("events")
                    .from((int) pageable.getOffset())
                    .size(pageable.getPageSize())
                    // 중복 제거를 위한 collapse 추가
                    .collapse(c -> c
                            .field("idx")
                    )
                    .query(q -> q
                            .bool(b -> b
                                    // 제목 필드 검색 (높은 가중치)
                                    .should(s -> s
                                            .match(m -> m
                                                    .field("title")
                                                    .query(keyword)
                                                    .boost(10.0f)
                                                    .analyzer("nori_analyzer") // korean_analyzer에서 변경
                                            )
                                    )
                                    // 정확한 매칭에 더 높은 가중치
                                    .should(s -> s
                                            .match(m -> m
                                                    .field("title.keyword") // title.exact에서 변경 (없을 경우)
                                                    .query(keyword)
                                                    .boost(50.0f)
                                            )
                                    )
                                    // 초성 검색 지원
                                    .should(s -> s
                                            .match(m -> m
                                                    .field("title")
                                                    .query(keyword)
                                                    .analyzer("nori_analyzer") // korean_analyzer에서 변경
                                            )
                                    )
                                    // 다른 필드 검색
                                    .should(s -> s
                                            .multiMatch(mm -> mm
                                                    .fields("postTitle", "postContent", "review")
                                                    .query(keyword)
                                                    .analyzer("nori_analyzer") // korean_analyzer에서 변경
                                            )
                                    )
                                    // 최소 매치 조건
                                    .minimumShouldMatch("1")
                            )
                    )
                    .highlight(h -> h
                            .fields("title", f -> f
                                    .preTags("<em>")
                                    .postTags("</em>")
                                    .fragmentSize(150)
                            )
                    )
                    .build();

            var response = client.search(searchRequest, EventDocument.class);

            // 결과 변환
            return response.hits().hits().stream()
                    .map(hit -> {
                        EventDocument doc = hit.source();
                        if (doc != null) {
                            return documentMapper.toEntity(doc);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    // 추가적인 중복 제거 (idx 기준)
                    .collect(Collectors.toMap(
                            Events::getIdx,  // 키 추출자
                            event -> event,  // 값 추출자
                            (existing, replacement) -> existing))  // 중복 시 첫 번째 요소 유지
                    .values()
                    .stream()
                    .collect(Collectors.toList());

        } catch (IOException e) {
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

        return searchRepository.findByCategoryAndTitleContaining(category, keyword)
                .stream()
                .map(documentMapper::toEntity)
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
            // 예외를 다시 던져서 호출자가 처리할 수 있도록 함
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
            // 예외를 다시 던져서 호출자가 처리할 수 있도록 함
            throw new RuntimeException("Failed to delete event document", e);
        }
    }
}