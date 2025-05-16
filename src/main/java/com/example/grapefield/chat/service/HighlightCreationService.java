package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatHighlight;
import com.example.grapefield.chat.model.entity.ChatMessageBase;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import com.example.grapefield.chat.model.response.ChatHighlightResp;
import com.example.grapefield.chat.model.response.HighlightDetectionResp;
import com.example.grapefield.chat.repository.ChatHighlightRepository;
import com.example.grapefield.chat.repository.ChatMessageBaseRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighlightCreationService {
    private final ChatHighlightRepository highlightRepository;
    private final ChatMessageBaseRepository baseRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KeywordExtractionService keywordExtractionService;
    private final TextCortexSummarizer summarizer;

    /**
     * 키워드 기반 하이라이트 생성
     */
    public ChatHighlight createHighlight(Long roomIdx, ChatMessageKafkaReq kafkaReq,
                                         HighlightDetectionResp detectionResp) throws Exception {
        // 키워드 추출
        // String keywords = keywordExtractionService.extractKeywords(detectionResp.getRecentMessages());

        String messagesInOneLine = summarizer.intoOneLine(detectionResp.getRecentMessages());
        String keywords = summarizer.summarize(messagesInOneLine);

        // 메트릭 정보를 포함한 설명 생성
        String description = keywordExtractionService.createDescription(keywords, detectionResp.getMetrics().getSpikeRatio());

        // DB에 저장
        ChatHighlight saved = saveHighlight(roomIdx, kafkaReq,
                (int) detectionResp.getMetrics().getCurrentMessageRate(), description);

        // WebSocket 브로드캐스트
        broadcastHighlight(roomIdx, saved);

        return saved;
    }

    /**
     * 하이라이트 DB 저장
     */
    private ChatHighlight saveHighlight(Long roomIdx, ChatMessageKafkaReq kafkaReq,
                                        int messageCount, String description) {
        try {
            ChatRoom room = chatRoomRepository.findById(roomIdx)
                    .orElseThrow(() -> new IllegalArgumentException("채팅방이 없습니다."));

            ChatMessageBase latestBase = baseRepository.findTopByRoomIdx(roomIdx)
                    .orElseThrow(() -> new IllegalStateException("기준 메시지를 찾을 수 없습니다."));

            // 🔧 개선된 시간 설정
            // 끝시간: 하이라이트 감지된 현재 시점
            LocalDateTime endTime = LocalDateTime.now();

            // 시작시간: 메시지 수에 따라 동적 계산
            int durationMinutes = calculateDuration(messageCount);
            LocalDateTime startTime = endTime.minusMinutes(durationMinutes);

            ChatHighlight highlight = ChatHighlight.builder()
                    .chatRoom(room)
                    .message(latestBase)
                    .startTime(startTime)
                    .endTime(endTime)
                    .messageCnt((long) messageCount)
                    .description(description)
                    .build();

            ChatHighlight saved = highlightRepository.save(highlight);
            log.info("💾 스마트 하이라이트 저장 완료 idx={}, roomIdx={}, 구간={}~{} ({}분), description={}",
                    saved.getIdx(), roomIdx,
                    startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    durationMinutes,
                    description);

            return saved;
        } catch (Exception e) {
            log.error("하이라이트 저장 중 오류 발생: roomIdx={}", roomIdx, e);
            throw new RuntimeException("하이라이트 저장 실패", e);
        }
    }

    /**
     * WebSocket 브로드캐스트
     */
    private void broadcastHighlight(Long roomIdx, ChatHighlight highlight) {
        try {
            log.info("🚀 브로드캐스트 시작: roomIdx={}, highlightIdx={}", roomIdx, highlight.getIdx());

            // 1. ChatHighlightResp 생성 확인
            ChatHighlightResp resp = ChatHighlightResp.fromEntity(highlight);
            log.info("✅ ChatHighlightResp 생성 완료: {}", resp);

            // 2. 브로드캐스트 대상 토픽 확인
            String topic = "/topic/chat.room.highlight." + roomIdx;
            log.info("📡 브로드캐스트 대상: {}", topic);

            // 3. SimpMessagingTemplate null 체크
            if (messagingTemplate == null) {
                log.error("❌ SimpMessagingTemplate이 null입니다!");
                return;
            }

            // 4. 실제 브로드캐스트 수행
            log.info("📤 브로드캐스트 수행 중...");
            messagingTemplate.convertAndSend(topic, resp);
            log.info("✅ 브로드캐스트 성공: roomIdx={}, topic={}", roomIdx, topic);

            // 5. 응답 객체 내용 확인
            log.info("📋 전송된 데이터: idx={}, description={}, startTime={}, endTime={}",
                    resp.getIdx(), resp.getDescription(), resp.getStartTime(), resp.getEndTime());

        } catch (Exception e) {
            log.error("💥 하이라이트 브로드캐스트 중 오류 발생: roomIdx={}", roomIdx, e);
            log.error("💥 상세 스택트레이스:", e);
        }
    }

    private int calculateDuration(int messageCount) {
        if (messageCount >= 50) {
            log.info("📊 매우 활발한 하이라이트: {}개 메시지 → 5분 구간", messageCount);
            return 5;   // 매우 활발: 5분
        } else if (messageCount >= 30) {
            log.info("📊 활발한 하이라이트: {}개 메시지 → 3분 구간", messageCount);
            return 3;   // 활발: 3분
        } else if (messageCount >= 15) {
            log.info("📊 보통 하이라이트: {}개 메시지 → 2분 구간", messageCount);
            return 2;   // 보통: 2분
        } else {
            log.info("📊 기본 하이라이트: {}개 메시지 → 1분 구간", messageCount);
            return 1;   // 기본: 1분
        }
    }
}
