package com.example.grapefield.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository  // 추가
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, String> {

    // 필드명을 수정된 카멜 케이스에 맞게 변경
    List<EventDocument> findByTitleContaining(String keyword);

    // postTitle, postContent 필드 검색 메소드 추가
    List<EventDocument> findByPostTitleContaining(String keyword);
    List<EventDocument> findByPostContentContaining(String keyword);

    // 리뷰 검색 메소드 추가
    List<EventDocument> findByReviewContaining(String keyword);

    // 다중 필드 검색 (OR 조건)
    List<EventDocument> findByTitleContainingOrPostTitleContainingOrPostContentContainingOrReviewContaining(
            String title, String postTitle, String postContent, String review);

    // 카테고리별 검색
    List<EventDocument> findByCategory(String category);

    // 카테고리 + 키워드 검색
    List<EventDocument> findByCategoryAndTitleContaining(String category, String title);

    // 커스텀 쿼리 - 모든 텍스트 필드에서 검색
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"postTitle\", \"postContent\", \"review\"]}}")
    Page<EventDocument> search(String keyword, Pageable pageable);
}