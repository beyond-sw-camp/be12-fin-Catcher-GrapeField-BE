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

    // 문서 인덱싱 (단일)
    public void indexEvent(Events event) {
        EventDocument document = documentMapper.toDocument(event);
        searchRepository.save(document);
    }

    // 여러 문서 인덱싱
    public void indexEvents(List<Events> events) {
        List<EventDocument> documents = events.stream()
                .map(documentMapper::toDocument)
                .collect(Collectors.toList());
        searchRepository.saveAll(documents);
    }


    // 키워드로 검색
    // 키워드로 검색 - 초성 검색과 짧은 단어 정확 매칭 개선
    public List<Events> searchByKeyword(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            System.out.println("Searching for keyword: " + keyword);

            // 검색 쿼리 단순화
            var searchRequest = new co.elastic.clients.elasticsearch.core.SearchRequest.Builder()
                    .index("events")
                    .from((int) pageable.getOffset())
                    .size(pageable.getPageSize())
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m
                                            .matchPhrase(mp -> mp
                                                    .field("title")
                                                    .query(keyword)
                                            )
                                    )

                                    .should(s -> s
                                            .matchPhrase(m -> m
                                                    .field("title")
                                                    .query(keyword)
                                                    .boost(50.0f)
                                            )
                                    )
                            )
                    ).build();

            var response = client.search(searchRequest, EventDocument.class);

            System.out.println("Total hits: " + response.hits().total().value());

            // 결과 변환
            return response.hits().hits().stream()
                    .map(hit -> {
                        EventDocument doc = hit.source();
                        if (doc != null) {
                            System.out.println("Found document: " + doc.getTitle() + " (ID: " + doc.getIdx() + ")     score:   " + hit.score());
                            return documentMapper.toEntity(doc);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("Elasticsearch search error: " + e.getMessage());
            e.printStackTrace();
            return List.of();
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
        return searchByKeyword(keyword, pageable);
    }

    // 문서 삭제
    public void deleteEventDocument(Long eventId) {
        searchRepository.deleteById(eventId.toString());
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

    // 한글 체크 함수
    private boolean isKorean(String text) {
        return text.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }
}