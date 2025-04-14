package com.example.grapefield.chat.config;

import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessageKafkaReq> kafkaListenerContainerFactory(
            ConsumerFactory<String, ChatMessageKafkaReq> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessageKafkaReq> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
