package com.example.grapefield.chat.config;

import com.example.grapefield.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class TopicInitializer {
    private final AdminClient adminClient;
    private final ChatRoomRepository chatRoomRepository;

    @Bean
    public ApplicationRunner createChatTopicsAtStartup() {
        return args -> {
            List<Long> chatRoomIdxs = chatRoomRepository.findAllChatRoomsByIdx();

            List<NewTopic> topics = chatRoomIdxs.stream()
                    .map(id -> new NewTopic("chat-" + id, 1, (short)1))
                    .collect(Collectors.toList());
            List<NewTopic> likeTopics = chatRoomIdxs.stream()
                    .map(id -> new NewTopic("chat-like-" + id, 1, (short)1))
                    .toList();
            topics.addAll(likeTopics);

//            CreateTopicsOptions options = new CreateTopicsOptions().validateOnly(true);
//
//            adminClient.createTopics(topics, options).all().get();

            try {
                adminClient.createTopics(topics).all().get();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof TopicExistsException) {
                    log.warn("이미 존재하는 토픽이 있어 무시합니다.", e.getCause());
                } else {
                    throw e;  // 다른 예외면 그대로 던짐
                }
            }
            log.info("✅ 애플리케이션 기동 시점 토픽 일괄 생성 완료: {} 개", topics.size());
        };
    }
}
