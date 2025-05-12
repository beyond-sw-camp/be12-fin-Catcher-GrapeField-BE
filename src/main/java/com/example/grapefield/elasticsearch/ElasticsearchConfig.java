package com.example.grapefield.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import java.io.IOException;
import java.io.StringReader;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.grapefield.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.host}")
    private String host;

    @Value("${spring.elasticsearch.port}")
    private int port;

    @Autowired
    private ElasticsearchClient client;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(host + ":" + port)
                .build();
    }

    @PostConstruct
    public void createNoriIndex() {
        try {
            boolean exists = client.indices().exists(req -> req.index("my_nori")).value();

            if (!exists) {
                String settings = """
                {
                  "settings": {
                    "analysis": {
                      "tokenizer": {
                        "nori_none": {
                          "type": "nori_tokenizer",
                          "decompound_mode": "none"
                        },
                        "nori_discard": {
                          "type": "nori_tokenizer",
                          "decompound_mode": "discard"
                        },
                        "nori_mixed": {
                          "type": "nori_tokenizer",
                          "decompound_mode": "mixed"
                        }
                      }
                    }
                  }
                }
                """;

                client.indices().create(c -> c
                        .index("my_nori")
                        .withJson(new StringReader(settings))
                );

                System.out.println("Nori 인덱스가 성공적으로 생성되었습니다.");
            } else {
                System.out.println("Nori 인덱스가 이미 존재합니다.");
            }
        } catch (Exception e) {
            System.err.println("Nori 인덱스 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}