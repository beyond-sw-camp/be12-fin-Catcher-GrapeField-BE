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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/elasticsearch")
public class ElasticsearchAdminController {

    private final EventSearchService searchService;
    private final co.elastic.clients.elasticsearch.ElasticsearchClient client;
    private final RestHighLevelClient restHighLevelClient; // 추가
    private final ObjectMapper objectMapper = new ObjectMapper(); // 추가

    @Autowired
    public ElasticsearchAdminController(
            EventSearchService searchService,
            co.elastic.clients.elasticsearch.ElasticsearchClient client,
            RestHighLevelClient restHighLevelClient) { // 생성자 매개변수 추가
        this.searchService = searchService;
        this.client = client;
        this.restHighLevelClient = restHighLevelClient; // 초기화
    }
    @GetMapping("/raw-search")
    public ResponseEntity<?> rawSearch(@RequestParam String keyword) {
        try {
            var searchRequest = new co.elastic.clients.elasticsearch.core.SearchRequest.Builder()
                    .index("events")
                    .query(q -> q.match(m -> m.field("title").query(keyword)))
                    .build();

            // 일반 Map으로 응답 받기
            var response = client.search(searchRequest, Map.class);

            // 응답 전체를 그대로 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
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
            @RequestParam String text,
            @RequestParam(defaultValue = "nori_analyzer") String analyzer) {
        try {
            Request request = new Request("POST", "/_analyze");
            String jsonBody = String.format("{\"analyzer\":\"%s\",\"text\":\"%s\"}", analyzer, text);
            request.setJsonEntity(jsonBody);

            Response response = restHighLevelClient.getLowLevelClient().performRequest(request);

            // 결과 파싱
            Map<String, Object> responseMap = objectMapper.readValue(
                    EntityUtils.toString(response.getEntity()),
                    new TypeReference<Map<String, Object>>() {});

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/create-template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createTemplate() {
        try {
            // nori-settings.json 파일 로드
            ClassPathResource resource = new ClassPathResource("nori-settings.json");
            String templateJson = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            // 템플릿 생성 요청
            Request request = new Request("PUT", "/_template/post_template");
            request.setJsonEntity(templateJson);

            Response response = restHighLevelClient.getLowLevelClient().performRequest(request);

            // 응답 처리
            Map<String, Object> responseMap = objectMapper.readValue(
                    EntityUtils.toString(response.getEntity()),
                    new TypeReference<Map<String, Object>>() {});

            return ResponseEntity.ok(responseMap);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // 기존 인덱스에 nori 설정 추가 메서드
    @PostMapping("/update-index/{indexName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateIndexSettings(
            @PathVariable String indexName,
            @RequestBody(required = false) String settingsJson) {
        try {
            // 설정 JSON이 제공되지 않은 경우 기본 nori 설정 사용
            if (settingsJson == null || settingsJson.isEmpty()) {
                settingsJson = "{\n" +
                        "  \"settings\": {\n" +
                        "    \"analysis\": {\n" +
                        "      \"analyzer\": {\n" +
                        "        \"nori_analyzer\": {\n" +
                        "          \"type\": \"custom\",\n" +
                        "          \"tokenizer\": \"nori_tokenizer\",\n" +
                        "          \"filter\": [\"nori_part_of_speech\", \"lowercase\", \"trim\"]\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"tokenizer\": {\n" +
                        "        \"nori_tokenizer\": {\n" +
                        "          \"type\": \"nori_tokenizer\",\n" +
                        "          \"decompound_mode\": \"mixed\",\n" +
                        "          \"discard_punctuation\": \"true\"\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"filter\": {\n" +
                        "        \"nori_part_of_speech\": {\n" +
                        "          \"type\": \"nori_part_of_speech\",\n" +
                        "          \"stoptags\": [\n" +
                        "            \"E\", \"IC\", \"J\", \"MAG\", \"MAJ\", \"MM\", \"SP\", \"SSC\", \"SSO\", \"SC\", \"SE\", \"XPN\", \"XSA\", \"XSN\", \"XSV\", \"UNA\", \"NA\", \"VSV\"\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            }

            // 인덱스 닫기 (설정 변경을 위해)
            Request closeRequest = new Request("POST", "/" + indexName + "/_close");
            restHighLevelClient.getLowLevelClient().performRequest(closeRequest);

            // 설정 업데이트
            Request updateRequest = new Request("PUT", "/" + indexName + "/_settings");
            updateRequest.setJsonEntity(settingsJson);
            Response updateResponse = restHighLevelClient.getLowLevelClient().performRequest(updateRequest);

            // 인덱스 다시 열기
            Request openRequest = new Request("POST", "/" + indexName + "/_open");
            restHighLevelClient.getLowLevelClient().performRequest(openRequest);

            // 응답 처리
            Map<String, Object> responseMap = objectMapper.readValue(
                    EntityUtils.toString(updateResponse.getEntity()),
                    new TypeReference<Map<String, Object>>() {});
            responseMap.put("message", "Index settings updated and index reopened");

            return ResponseEntity.ok(responseMap);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}