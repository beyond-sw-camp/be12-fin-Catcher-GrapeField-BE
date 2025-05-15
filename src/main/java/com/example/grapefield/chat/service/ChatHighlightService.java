package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatHighlight;
import com.example.grapefield.chat.model.entity.ChatMessageBase;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.response.ChatHighlightResp;
import com.example.grapefield.chat.repository.ChatHighlightRepository;
import com.example.grapefield.chat.repository.ChatMessageBaseRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.kafka.common.requests.FetchMetadata.log;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatHighlightService {

    private final MessageTrackingService messageTrackingService;
    private final HighlightDetectionService highlightDetectionService;
    private final HighlightCreationService highlightCreationService;

    /**
     * 메시지 추적 및 하이라이트 감지 (기존 인터페이스 유지)
     */
    public void trackMessage(ChatMessageKafkaReq kafkaReq) {
        Long roomIdx = kafkaReq.getRoomIdx();
        long currentTime = System.currentTimeMillis();

        log.info("🚀 하이라이트 처리 시작: roomIdx={}, userIdx={}",
                roomIdx, kafkaReq.getSendUserIdx());

        try {
            // 1. 메시지 추적
            messageTrackingService.trackMessage(kafkaReq);

            // 2. 하이라이트 감지
            Optional<HighlightDetectionService.HighlightDetectionResult> detectionResult =
                    highlightDetectionService.detectHighlight(roomIdx, currentTime);

            // 3. 감지되면 하이라이트 생성
            if (detectionResult.isPresent()) {
                log.info("✨ 하이라이트 생성 시작: roomIdx={}", roomIdx);
                ChatHighlight highlight = highlightCreationService.createHighlight(
                        roomIdx, kafkaReq, detectionResult.get());

                log.info("🎉 하이라이트 생성 완료: roomIdx={}, idx={}, description={}",
                        roomIdx, highlight.getIdx(), highlight.getDescription());
            } else {
                log.debug("⏸️ 하이라이트 미생성: roomIdx={}", roomIdx);
            }

        } catch (Exception e) {
            log.error("하이라이트 처리 중 오류 발생: roomIdx={}", roomIdx, e);
        }
    }
}
