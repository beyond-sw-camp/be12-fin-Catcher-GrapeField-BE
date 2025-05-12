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
        document.setPostTitle(event.getTitle());
        document.setPostContent(event.getDescription());
        document.setReview("");
        return document;
    }

    // ES 문서 → JPA 엔티티 (ID로 매핑)
    public Events toEntity(EventDocument document) {
        EventCategory category;
        try {
            // 카테고리 이름 정규화: 공백 제거, 대문자로 변환
            String normalizedCategory = document.getCategory() != null ?
                    document.getCategory().trim().toUpperCase() : null;

            // "콘서트" -> "CONCERT" 등 한글 카테고리를 영문 Enum으로 매핑
            if (normalizedCategory != null) {
                if (normalizedCategory.equals("콘서트")) {
                    normalizedCategory = "CONCERT";
                }
                // 필요 시 다른 매핑 추가
            }

            category = normalizedCategory != null ?
                    EventCategory.valueOf(normalizedCategory) : EventCategory.ALL;
        } catch (IllegalArgumentException e) {
            // 매핑 실패 시 기본값 사용
            category = EventCategory.ALL;
            System.out.println("Unknown category: " + document.getCategory() + ", using default: ALL");
        }

        return Events.builder()
                .idx(document.getIdx() != null ? Long.parseLong(document.getIdx()) : null)
                .title(document.getTitle())
                .category(category)
                .description(document.getPostContent())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .posterImgUrl("")
                .notification("")
                .venue("")
                .runningTime(0)
                .ageLimit(AgeLimit.ALL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isVisible(true)
                .build();
    }
}