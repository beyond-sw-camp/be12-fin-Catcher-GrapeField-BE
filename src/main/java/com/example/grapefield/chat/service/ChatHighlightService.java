package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatHighlight;
import com.example.grapefield.chat.model.entity.ChatMessageBase;
import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.response.ChatHighlightResp;
import com.example.grapefield.chat.repository.ChatHighlightRepository;
import com.example.grapefield.chat.repository.ChatMessageBaseRepository;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.kafka.common.requests.FetchMetadata.log;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatHighlightService {

    private final ChatHighlightRepository highlightRepository;
    private final ChatMessageBaseRepository baseRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // roomIdx -> 메시지 타임스탬프 목록
    private final Map<Long, List<Long>> messageTimestamps = new ConcurrentHashMap<>();
    private final ChatMessageCurrentRepository chatMessageCurrentRepository;
    private final ChatHighlightRepository chatHighlightRepository;

    public void trackMessage(ChatMessageKafkaReq kafkaReq) {
        Long roomIdx = kafkaReq.getRoomIdx();
        long now = System.currentTimeMillis();

        messageTimestamps.putIfAbsent(roomIdx, new ArrayList<>());
        List<Long> ts = messageTimestamps.get(roomIdx);
        ts.add(now);
        ts.removeIf(t -> now - t > 30_000);

        if (ts.size() >= 20) {
            log.info("🔥 하이라이트 감지! roomIdx={}, count={}", roomIdx, ts.size());
            saveHighlight(roomIdx, kafkaReq, ts.size());
            ts.clear();
        }
    }

    private void saveHighlight(Long roomIdx, ChatMessageKafkaReq kafkaReq, int count) {
        ChatRoom room = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 없습니다."));

        // native query로 Base를 직접 조회
        ChatMessageBase latestBase = baseRepository.findTopByRoomIdx(roomIdx)
                .orElseThrow(() -> new IllegalStateException("기준 메시지를 찾을 수 없습니다."));

        LocalDateTime endTime = latestBase.getCreatedAt();
        LocalDateTime startTime = endTime.minusSeconds(10);

        String description = kafkaReq.getContent();

        ChatHighlight highlight = ChatHighlight.builder()
                .chatRoom(room)
                .message(latestBase)
                .startTime(startTime)
                .endTime(endTime)
                .messageCnt((long) count)
                .description(description)
                .build();

        ChatHighlight saved = highlightRepository.save(highlight);
        log.info("💾 저장 완료 idx={}, roomIdx={}", saved.getIdx(), roomIdx);

        ChatHighlightResp resp = ChatHighlightResp.fromEntity(saved);
        messagingTemplate.convertAndSend(
                "/topic/chat.room.highlight." + roomIdx,
                resp
        );
        log.info("📡 브로드캐스트 완료 roomIdx={}, highlightIdx={}", roomIdx, saved.getIdx());
    }

    private void saveHighlightIfNotExists(Long roomIdx, ChatMessageKafkaReq kafkaReq, int count) {
        ChatMessageCurrent msgCurrent = chatMessageCurrentRepository.findByMessageUuid(kafkaReq.getMessageUuid());
        if (chatHighlightRepository.existsById(msgCurrent.getMessageIdx())){
            log.info("🔔이미 저장된 하이라이트입니다.. 중복 저장하지 않습니다.. messageUuid={}", msgCurrent.getMessageUuid());
            return ;
        }

        ChatRoom room = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 없습니다."));

        // native query로 Base를 직접 조회
        ChatMessageBase latestBase = baseRepository.findTopByRoomIdx(roomIdx)
                .orElseThrow(() -> new IllegalStateException("기준 메시지를 찾을 수 없습니다."));

        LocalDateTime endTime = latestBase.getCreatedAt();
        LocalDateTime startTime = endTime.minusSeconds(10);

        String description = kafkaReq.getContent();

        ChatHighlight highlight = ChatHighlight.builder()
                .chatRoom(room)
                .message(latestBase)
                .startTime(startTime)
                .endTime(endTime)
                .messageCnt((long) count)
                .description(description)
                .build();

        ChatHighlight saved = highlightRepository.save(highlight);
        log.info("💾 저장 완료 idx={}, roomIdx={}", saved.getIdx(), roomIdx);

        ChatHighlightResp resp = ChatHighlightResp.fromEntity(saved);
        messagingTemplate.convertAndSend(
                "/topic/chat.room.highlight." + roomIdx,
                resp
        );
        log.info("📡 브로드캐스트 완료 roomIdx={}, highlightIdx={}", roomIdx, saved.getIdx());
    }
}
