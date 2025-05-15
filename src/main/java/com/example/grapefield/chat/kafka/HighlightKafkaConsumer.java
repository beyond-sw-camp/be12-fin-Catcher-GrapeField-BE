package com.example.grapefield.chat.kafka;

import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.service.ChatHighlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HighlightKafkaConsumer {

    private final ChatHighlightService chatHighlightService;

    @KafkaListener(
            topicPattern = "^chat-\\d+$",
            groupId = "${spring.kafka.consumer.highlight.group-id}",
            containerFactory = "highlightKafkaListenerContainerFactory"
    )
    public void consumeHighlight(ChatMessageKafkaReq message) {
        log.info("[HIGHLIGHT LISTENER 🔍] 메시지 받음 roomIdx={}, content='{}'",
                message.getRoomIdx(), message.getContent());
        chatHighlightService.trackMessage(message);
    }
}
