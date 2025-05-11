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
import java.util.Map;

@RestController
@RequestMapping("/admin/elasticsearch")
public class ElasticsearchAdminController {

    private final DatabaseToElasticsearchMigrator migrator;
    private final EventSearchService searchService;

    @Autowired
    public ElasticsearchAdminController(
            DatabaseToElasticsearchMigrator migrator,
            EventSearchService searchService) {
        this.migrator = migrator;
        this.searchService = searchService;
    }

    @PostMapping("/reindex")
    @PreAuthorize("hasRole('ADMIN')") // 시큐리티 설정에 따라 조정
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

    @PostMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDocumentCount() {
        try {
            long count = searchService.getDocumentCount();

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