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

    @Value("${elasticsearch.host:${ELASTIC_HOST}}")
    private String host;

    @Value("${elasticsearch.port:${ELASTIC_PORT}}")
    private String portStr;

    private int port;

    @Value("${elasticsearch.username:${ELASTIC_USER}}")
    private String username;

    @Value("${elasticsearch.password:${ELASTIC_PASSWORD}}")
    private String password;

    @PostConstruct
    public void init() {
        try {
            this.port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            // URL 형식이면 포트만 추출
            if (portStr.contains("://")) {
                String[] parts = portStr.split(":");
                this.port = Integer.parseInt(parts[parts.length-1]);
            } else {
                // 기본값 설정
                this.port = 9200;
            }
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
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        return new RestHighLevelClient(builder);
    }

    // 최신 ElasticsearchClient Bean 생성
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 레거시 RestClient 생성
        RestClient restClient = restHighLevelClient().getLowLevelClient();

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