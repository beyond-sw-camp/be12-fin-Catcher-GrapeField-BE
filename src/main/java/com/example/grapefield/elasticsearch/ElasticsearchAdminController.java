package com.example.grapefield.elasticsearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/elasticsearch")
public class ElasticsearchAdminController {

    private final DatabaseToElasticsearchMigrator migrator;
    private final EventSearchService searchService;
    private final co.elastic.clients.elasticsearch.ElasticsearchClient client; // 추가된 필드

    @Autowired
    public ElasticsearchAdminController(
            DatabaseToElasticsearchMigrator migrator,
            EventSearchService searchService,
            co.elastic.clients.elasticsearch.ElasticsearchClient client) { // 생성자 매개변수 추가
        this.migrator = migrator;
        this.searchService = searchService;
        this.client = client; // 초기화
    }

    @GetMapping("/simple-test")
    public ResponseEntity<?> simpleTest() {
        try {
            // 단순 인덱스 존재 확인
            boolean indexExists = client.indices().exists(r -> r.index("events")).value();

            // 기본 검색 (response 파싱 없이)
            var searchRequest = new co.elastic.clients.elasticsearch.core.SearchRequest.Builder()
                    .index("events")
                    .size(1)
                    .build();

            var searchResponse = client.search(searchRequest, Map.class);

            Map<String, Object> result = new HashMap<>();
            result.put("indexExists", indexExists);
            result.put("responseReceived", true);
            result.put("totalHits", searchResponse.hits().total().value());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace(); // 전체 스택 트레이스 로깅
            return ResponseEntity.status(500).body("Error: " + e.getMessage() + ", Type: " + e.getClass().getName());
        }
    }

    @GetMapping("/test-elasticsearch")
    public ResponseEntity<?> testElasticsearch() {
        try {
            // 1. 연결 테스트
            boolean indexExists = client.indices().exists(r -> r.index("events")).value();

            // 2. 간단한 검색 테스트
            var searchRequest = new co.elastic.clients.elasticsearch.core.SearchRequest.Builder()
                    .index("events")
                    .size(3)
                    .build();

            var response = client.search(searchRequest, EventDocument.class);
            List<EventDocument> docs = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .filter(obj -> Objects.nonNull(obj))
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("indexExists", indexExists);
            result.put("hitCount", response.hits().total().value());
            result.put("samples", docs);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/reindex")
//    @PreAuthorize("hasRole('ADMIN')") // 시큐리티 설정에 따라 조정
    public ResponseEntity<Map<String, Object>> reindexAll() {

        try {
            migrator.migrateAllData();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reindexing completed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Reindexing failed: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getIndexStatus() {
        Map<String, Object> status = new HashMap<>();

        // 문서 수 조회
        long count = searchService.countDocuments();
        status.put("documentCount", count);

        // 최근 문서 5개 샘플 조회
        List<EventDocument> samples = searchService.getSampleDocuments(20);
        status.put("sampleDocuments", samples);

        return ResponseEntity.ok(status);
    }

    @PostMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDocumentCount() {
        try {
            long count = searchService.countDocuments();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get document count: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ElasticsearchAdminController.java 수정
    @GetMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeText(
            @RequestParam String text) {
        try {
            // 인덱스를 지정하여 분석 요청
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http")));

            Request request = new Request("GET", "/events/_analyze");
            request.setJsonEntity("{\"analyzer\":\"korean_analyzer\",\"text\":\"" + text + "\"}");

            Response response = client.getLowLevelClient().performRequest(request);

            // 결과 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(
                    EntityUtils.toString(response.getEntity()),
                    new TypeReference<Map<String, Object>>() {});

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}