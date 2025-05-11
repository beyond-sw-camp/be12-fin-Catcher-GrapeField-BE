package com.example.grapefield.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class ElasticsearchIndexInitializer {

    private final ElasticsearchClient client;

    @Autowired
    public ElasticsearchIndexInitializer(ElasticsearchClient client) {
        this.client = client;
    }

    @PostConstruct
    public void initIndex() throws IOException {
        // 인덱스 존재 여부 확인
        BooleanResponse exists = client.indices().exists(e -> e.index("events"));

        if (!exists.value()) {
            // 인덱스 설정 생성
            Map<String, Object> settings = new HashMap<>();
            Map<String, Object> indexSettings = new HashMap<>();
            indexSettings.put("number_of_shards", 3);
            indexSettings.put("number_of_replicas", 1);

            // 분석기 설정
            Map<String, Object> analysis = new HashMap<>();
            Map<String, Object> analyzer = new HashMap<>();

            Map<String, Object> koreanAnalyzer = new HashMap<>();
            koreanAnalyzer.put("tokenizer", "standard");
            koreanAnalyzer.put("filter", Arrays.asList("lowercase", "choseong_filter", "jamo_filter", "kor2eng_filter", "eng2kor_filter"));

            analyzer.put("korean_analyzer", koreanAnalyzer);
            analysis.put("analyzer", analyzer);
            indexSettings.put("analysis", analysis);

            settings.put("settings", indexSettings);

            // 매핑 설정
            Map<String, Object> mappings = new HashMap<>();
            Map<String, Object> properties = new HashMap<>();

            // title 필드 (exact 서브필드 포함)
            Map<String, Object> title = new HashMap<>();
            title.put("type", "text");
            title.put("analyzer", "korean_analyzer");

            // exact 서브필드 추가
            Map<String, Object> titleFields = new HashMap<>();
            Map<String, Object> titleExact = new HashMap<>();
            titleExact.put("type", "text");
            titleExact.put("analyzer", "standard");
            titleFields.put("exact", titleExact);
            title.put("fields", titleFields);

            properties.put("title", title);

            // category 필드
            Map<String, Object> category = new HashMap<>();
            category.put("type", "keyword");
            properties.put("category", category);

            // postTitle 필드 (exact 서브필드 포함)
            Map<String, Object> postTitle = new HashMap<>();
            postTitle.put("type", "text");
            postTitle.put("analyzer", "korean_analyzer");

            // exact 서브필드 추가
            Map<String, Object> postTitleFields = new HashMap<>();
            Map<String, Object> postTitleExact = new HashMap<>();
            postTitleExact.put("type", "text");
            postTitleExact.put("analyzer", "standard");
            postTitleFields.put("exact", postTitleExact);
            postTitle.put("fields", postTitleFields);

            properties.put("postTitle", postTitle);

            // postContent 필드 (exact 서브필드 포함)
            Map<String, Object> postContent = new HashMap<>();
            postContent.put("type", "text");
            postContent.put("analyzer", "korean_analyzer");

            // exact 서브필드 추가
            Map<String, Object> postContentFields = new HashMap<>();
            Map<String, Object> postContentExact = new HashMap<>();
            postContentExact.put("type", "text");
            postContentExact.put("analyzer", "standard");
            postContentFields.put("exact", postContentExact);
            postContent.put("fields", postContentFields);

            properties.put("postContent", postContent);

            // review 필드 (exact 서브필드 포함)
            Map<String, Object> review = new HashMap<>();
            review.put("type", "text");
            review.put("analyzer", "korean_analyzer");

            // exact 서브필드 추가
            Map<String, Object> reviewFields = new HashMap<>();
            Map<String, Object> reviewExact = new HashMap<>();
            reviewExact.put("type", "text");
            reviewExact.put("analyzer", "standard");
            reviewFields.put("exact", reviewExact);
            review.put("fields", reviewFields);

            properties.put("review", review);

            // 매핑 구성
            Map<String, Object> mapping = new HashMap<>();
            mapping.put("properties", properties);

            // 최종 구성
            Map<String, Object> indexConfig = new HashMap<>();
            indexConfig.put("settings", indexSettings);
            indexConfig.put("mappings", mapping);

            // JSON으로 변환하여 인덱스 생성
            ObjectMapper objectMapper = new ObjectMapper();
            String indexConfigJson = objectMapper.writeValueAsString(indexConfig);

            // 인덱스 생성
            client.indices().create(c -> c
                    .index("events")
                    .withJson(new StringReader(indexConfigJson))
            );

            System.out.println("Created index 'events' with settings and mappings");
        }
    }
}