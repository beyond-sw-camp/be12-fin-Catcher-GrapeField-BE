package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.kafka.common.requests.FetchMetadata.log;

@Service
public class ChatHighlightService {
    private final Map<Long, List<Long>> messageTimestamps = new ConcurrentHashMap<>();

    public void trackMessage(ChatMessageKafkaReq message) {
        Long roomIdx = message.getRoomIdx();
        long now = System.currentTimeMillis();

        messageTimestamps.putIfAbsent(roomIdx, new ArrayList<>());
        List<Long> timestamps = messageTimestamps.get(roomIdx);
        timestamps.add(now);

        // 최근 10초 이내 메시지만 필터
        timestamps.removeIf(t -> now - t > 20_000);

        if (timestamps.size() >= 20) {
            log.info("🔥 하이라이트 감지! roomIdx={}, 최근 10초 메시지 수: {}", roomIdx, timestamps.size());
            // TODO: DB 저장 or 알림 or WebSocket 전송
        }
    }
}
