package com.example.grapefield.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host:${ELASTIC_HOST:localhost}}")
    private String host;

    @Value("${elasticsearch.port:${ELASTIC_PORT:9200}}")
    private String portStr;

    // 여기에 port 변수 추가
    private int port;

    @Value("${elasticsearch.username:${ELASTIC_USER:elastic}}")
    private String username;

    @Value("${elasticsearch.password:${ELASTIC_PASSWORD:qwer1234}}")
    private String password;

    @PostConstruct
    public void init() {
        try {
            this.port = Integer.parseInt(portStr);
            // 디버깅용 로그 추가
            System.out.println("Elasticsearch 설정:");
            System.out.println("Host: " + host);
            System.out.println("Port: " + port);
            System.out.println("Username: " + username);
        } catch (NumberFormatException e) {
            // URL 형식이면 포트만 추출
            if (portStr.contains("://")) {
                String[] parts = portStr.split(":");
                this.port = Integer.parseInt(parts[parts.length-1]);
            } else {
                // 기본값 설정
                this.port = 9200;
            }
            System.out.println("Port 파싱 오류, 기본값 9200 사용: " + e.getMessage());
        }
    }

    // 레거시 RestHighLevelClient Bean 생성
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(
                        new HttpHost(host, port, "http"))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder
                                .setConnectTimeout(5000)
                                .setSocketTimeout(10000));

        return new RestHighLevelClient(builder);
    }

    // 최신 ElasticsearchClient Bean 생성
    @Bean
    public ElasticsearchClient elasticsearchClient(RestHighLevelClient restHighLevelClient) {
        // RestHighLevelClient에서 RestClient 가져오기
        RestClient restClient = restHighLevelClient.getLowLevelClient();

        // ElasticsearchClient 생성
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }

    // elasticsearchTemplate 빈 생성 (Spring Data Elasticsearch와 호환)
    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(ElasticsearchClient elasticsearchClient) {
        return new ElasticsearchTemplate(elasticsearchClient);
    }
}