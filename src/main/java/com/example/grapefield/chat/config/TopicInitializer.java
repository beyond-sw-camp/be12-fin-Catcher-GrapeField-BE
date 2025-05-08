package com.example.grapefield.chat.config;

import com.example.grapefield.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
        try {
          List<Long> chatRoomIdxs = chatRoomRepository.findAllChatRoomsByIdx();

          List<NewTopic> topics = chatRoomIdxs.stream()
              .map(id -> new NewTopic("chat-" + id, 1, (short)1))
              .collect(Collectors.toList());
          List<NewTopic> likeTopics = chatRoomIdxs.stream()
              .map(id -> new NewTopic("chat-like-" + id, 1, (short)1))
              .toList();
          topics.addAll(likeTopics);

          // 타임아웃 설정 추가
          CreateTopicsOptions options = new CreateTopicsOptions()
              .timeoutMs(10000); // 10초 타임아웃

          try {
            adminClient.createTopics(topics, options).all().get(15, TimeUnit.SECONDS);
            log.info("✅ 애플리케이션 기동 시점 토픽 일괄 생성 완료: {} 개", topics.size());
          } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
              log.warn("이미 존재하는 토픽이 있어 무시합니다.", e.getCause());
            } else {
              log.error("Kafka 토픽 생성 중 오류 발생: {}", e.getMessage());
              // 오류를 기록하지만 애플리케이션 시작은 막지 않음
            }
          } catch (TimeoutException e) {
            log.error("Kafka 토픽 생성 타임아웃: {}", e.getMessage());
            // 타임아웃 오류를 기록하지만 애플리케이션 시작은 막지 않음
          }
        } catch (Exception e) {
          // 모든 다른 예외도 잡아서 애플리케이션 시작을 방해하지 않도록 함
          log.error("토픽 초기화 중 예상치 못한 오류 발생: {}", e.getMessage());
        }
      };
    }
}
