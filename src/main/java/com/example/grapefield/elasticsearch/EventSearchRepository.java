package com.example.grapefield.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, String> {

    // 기본 메서드 - 기존 코드 유지
    List<EventDocument> findByTitleContaining(String keyword);
    List<EventDocument> findByPostTitleContaining(String keyword);
    List<EventDocument> findByPostContentContaining(String keyword);
    List<EventDocument> findByReviewContaining(String keyword);
    List<EventDocument> findByCategory(String category);
    List<EventDocument> findByCategoryAndTitleContaining(String category, String title);

    // 다중 필드 검색 (OR 조건)
    List<EventDocument> findByTitleContainingOrPostTitleContainingOrPostContentContainingOrReviewContaining(
            String title, String postTitle, String postContent, String review);

    // Nori 분석기를 활용한 커스텀 쿼리 추가

    // 모든 텍스트 필드에서 검색 (Nori 분석기 활용)
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"postTitle\", \"postContent\", \"review\"], \"analyzer\": \"korean_analyzer\"}}")
    Page<EventDocument> search(String keyword, Pageable pageable);

    // 제목 필드 검색 (Nori 분석기 활용)
    @Query("{\"match\": {\"title\": {\"query\": \"?0\", \"analyzer\": \"korean_analyzer\"}}}")
    List<EventDocument> searchByTitle(String keyword);

    // 복합 쿼리 - 제목에 높은 가중치, 본문에 낮은 가중치
    @Query("{\"bool\": {\"should\": [" +
            "{\"match\": {\"title\": {\"query\": \"?0\", \"boost\": 3.0, \"analyzer\": \"korean_analyzer\"}}}," +
            "{\"match\": {\"postTitle\": {\"query\": \"?0\", \"boost\": 2.0, \"analyzer\": \"korean_analyzer\"}}}," +
            "{\"match\": {\"postContent\": {\"query\": \"?0\", \"boost\": 1.0, \"analyzer\": \"korean_analyzer\"}}}," +
            "{\"match\": {\"review\": {\"query\": \"?0\", \"boost\": 1.0, \"analyzer\": \"korean_analyzer\"}}}" +
            "]}}")
    Page<EventDocument> searchWithBoost(String keyword, Pageable pageable);

    // 초성 검색을 위한 메서드 (예: ㅎㄱㄷ -> 한국대학)
    @Query("{\"match\": {\"title.nori_mixed\": {\"query\": \"?0\"}}}")
    List<EventDocument> searchByInitial(String initial);

    // 카테고리 필터링 + 키워드 검색
    @Query("{\"bool\": {\"must\": [" +
            "{\"term\": {\"category\": \"?0\"}}," +
            "{\"multi_match\": {\"query\": \"?1\", \"fields\": [\"title\", \"postTitle\", \"postContent\", \"review\"], \"analyzer\": \"korean_analyzer\"}}" +
            "]}}")
    Page<EventDocument> searchByKeywordAndCategory(String category, String keyword, Pageable pageable);
}