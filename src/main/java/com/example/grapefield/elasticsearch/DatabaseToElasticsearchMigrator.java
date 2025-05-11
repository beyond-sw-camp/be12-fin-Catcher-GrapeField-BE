package com.example.grapefield.elasticsearch;

import com.example.grapefield.events.model.entity.Events;

import com.example.grapefield.events.repository.EventsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseToElasticsearchMigrator {
    private static final Logger log = LoggerFactory.getLogger(DatabaseToElasticsearchMigrator.class);

    private final EventsRepository eventRepository; // JPA 저장소
    private final EventSearchService searchService;

    @Autowired
    public DatabaseToElasticsearchMigrator(
            EventsRepository eventRepository,
            EventSearchService searchService) {
        this.eventRepository = eventRepository;
        this.searchService = searchService;
    }

    // 애플리케이션 시작 시 자동으로 마이그레이션을 수행하려면 주석 해제
    // @PostConstruct
    public void migrateAllData() {
        log.info("Starting data migration from database to Elasticsearch");

        // 페이징 처리를 통한 대량 데이터 마이그레이션
        int pageSize = 100;
        long totalItems = eventRepository.count();
        long totalPages = (totalItems + pageSize - 1) / pageSize;

        log.info("Total items to migrate: {}", totalItems);

        for (int page = 0; page < totalPages; page++) {
            Pageable pageable = PageRequest.of(page, pageSize);
            List<Events> events = eventRepository.findAll(pageable).getContent();

            searchService.indexEvents(events);

            log.info("Migrated page {} of {} ({} items)", page + 1, totalPages, events.size());
        }

        log.info("Migration completed: {} events indexed", totalItems);
    }
}