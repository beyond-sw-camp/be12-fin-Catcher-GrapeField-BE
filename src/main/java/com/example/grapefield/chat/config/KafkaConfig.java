package com.example.grapefield.chat.config;

import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.response.ChatMessageResp;
import com.example.grapefield.chat.model.response.ChatParticipantEventResp;
import com.example.grapefield.chat.model.response.UserChatListEventResp;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // 브로드캐스트용 group-id (ConfigMap으로 인스턴스별 분리)
    @Value("${spring.kafka.consumer.chat.group-id}")
    private String chatGroupId;
    @Value("${spring.kafka.consumer.heart.group-id}")
    private String heartGroupId;
    @Value("${spring.kafka.consumer.highlight.group-id}")
    private String highlightGroupId;

    // ProducerFactory & KafkaTemplate
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32_768);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        return new DefaultKafkaProducerFactory<>(props);
    }


    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public Map<String, Object> commonConsumerProps(@Value("${spring.kafka.bootstrap-servers}") String servers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }



    /* DTO를 구분하여 멀티팩토리를 만들어줌*/


    // 1) 채팅 메시지용 ConsumerFactory
    @Bean
    public ConsumerFactory<String, ChatMessageKafkaReq> messageConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, chatGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // JsonDeserializer를 직접 생성하여 설정
        JsonDeserializer<ChatMessageKafkaReq> deserializer =
                new JsonDeserializer<>(ChatMessageKafkaReq.class, false);
        deserializer.addTrustedPackages("com.example.grapefield.chat.model.request");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessageKafkaReq>
    chatKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessageKafkaReq> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(messageConsumerFactory());
        return factory;
    }

    // 2) 하트 이벤트용 ConsumerFactory
    @Bean
    public ConsumerFactory<String, ChatHeartKafkaReq> heartConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, heartGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // JsonDeserializer를 직접 생성하여 설정
        JsonDeserializer<ChatHeartKafkaReq> deserializer =
                new JsonDeserializer<>(ChatHeartKafkaReq.class, false);
        deserializer.addTrustedPackages("com.example.grapefield.chat.model.request");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatHeartKafkaReq>
    heartKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatHeartKafkaReq> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(heartConsumerFactory());
        return factory;
    }


    // 하이라이트 감지
    @Bean
    public ConsumerFactory<String, ChatMessageKafkaReq> highlightConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, highlightGroupId); // 💡 그룹 다르게
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<ChatMessageKafkaReq> deserializer =
                new JsonDeserializer<>(ChatMessageKafkaReq.class, false);
        deserializer.addTrustedPackages("com.example.grapefield.chat.model.request");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessageKafkaReq>
    highlightKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessageKafkaReq> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(highlightConsumerFactory());
        return factory;
    }

    /*
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessageKafkaReq> kafkaListenerContainerFactory(
            ConsumerFactory<String, ChatMessageKafkaReq> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessageKafkaReq> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
    */

    // 사용자 이벤트용 ConsumerFactory 추가
    @Bean
    public ConsumerFactory<String, UserChatListEventResp> userEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-event-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.grapefield.chat.model.response.UserChatListEventResp");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(UserChatListEventResp.class))
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserChatListEventResp>
    userEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserChatListEventResp> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userEventConsumerFactory());
        return factory;
    }

    // 참여자 이벤트용
    @Bean
    public ConsumerFactory<String, ChatParticipantEventResp> participantEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-participant-group");

        // ErrorHandlingDeserializer로 wrapping
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // 내부적으로 JsonDeserializer 사용
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.grapefield.chat.model.response.ChatParticipantEventResp");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // 보안을 고려하면 패키지 명시도 OK

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(ChatParticipantEventResp.class))
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatParticipantEventResp>
    participantEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatParticipantEventResp> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(participantEventConsumerFactory());
        return factory;
    }
}
