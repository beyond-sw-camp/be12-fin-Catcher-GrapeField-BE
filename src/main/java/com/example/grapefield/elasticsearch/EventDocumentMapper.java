package com.example.grapefield.elasticsearch;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.AgeLimit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventDocumentMapper {

    // JPA 엔티티 → ES 문서
    public EventDocument toDocument(Events event) {
        EventDocument document = new EventDocument();
        document.setIdx(event.getIdx().toString());
        document.setTitle(event.getTitle());
        document.setCategory(event.getCategory().name());  // Enum을 String으로 변환
        document.setPostTitle(event.getTitle());  // 또는 다른 적절한 필드
        document.setPostContent(event.getDescription());
        document.setReview("");  // 리뷰는 빈 값으로 설정하거나 필요에 따라 채움
        return document;
    }

    // ES 문서 → JPA 엔티티 (ID로 매핑)
    public Events toEntity(EventDocument document) {
        // 빌더 패턴 사용
        return Events.builder()
                .idx(document.getIdx() != null ? Long.parseLong(document.getIdx()) : null)
                .title(document.getTitle())
                .category(document.getCategory() != null ? EventCategory.valueOf(document.getCategory()) : null)
                .description(document.getPostContent())
                // 아래는 필수 필드인 경우 기본값 설정
                .startDate(LocalDateTime.now())  // 기본값 또는 null
                .endDate(LocalDateTime.now().plusDays(30))  // 기본값 또는 null
                .posterImgUrl("")  // 기본값 또는 null
                .notification("")  // 기본값 또는 null
                .venue("")  // 기본값 또는 null
                .runningTime(0)  // 기본값 또는 null
                .ageLimit(AgeLimit.ALL)  // 기본값 또는 null
                .createdAt(LocalDateTime.now())  // 기본값
                .updatedAt(LocalDateTime.now())  // 기본값
                .isVisible(true)  // 기본값
                // 관계 필드는 일반적으로 매핑하지 않음
                .build();
    }
}