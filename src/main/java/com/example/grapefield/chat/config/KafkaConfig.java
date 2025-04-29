package com.example.grapefield.chat.config;

import com.example.grapefield.chat.model.request.ChatHeartKafkaReq;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.response.ChatMessageResp;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-group");
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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-like-group");
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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-highlight-group"); // 💡 그룹 다르게
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
}
