package com.example.grapefield.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighlightDetectionService {

    private final MessageTrackingService messageTrackingService;
    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    // 감지 설정
//    private static final double SPIKE_THRESHOLD = 2.5; // 평균의 2.5배 이상
//    private static final int MIN_MESSAGES_FOR_HIGHLIGHT = 10; // 최소 메시지 수
//    private static final int COOLDOWN_SECONDS = 120; // 하이라이트 생성 후 쿨다운

    // 테스트용으로 임계값 낮춤
    private static final double SPIKE_THRESHOLD = 0.5; // 2.5 → 1.2로 낮춤
    private static final int MIN_MESSAGES_FOR_HIGHLIGHT = 1; // 10 → 3으로 낮춤
    private static final int COOLDOWN_SECONDS = 10; // 120 → 30초로 단축

    private static final String HIGHLIGHT_COOLDOWN_KEY = "room:{roomIdx}:highlight_cooldown";

    /**
     * 하이라이트 감지 로직
     */
    public Optional<HighlightDetectionResult> detectHighlight(Long roomIdx, long currentTime) {
        log.info("🔍 하이라이트 감지 시작: roomIdx={}", roomIdx);
        // 쿨다운 체크
        if (!checkCooldown(roomIdx)) {
            log.info("❄️ 쿨다운 중이라 스킵: roomIdx={}", roomIdx);
            return Optional.empty();
        }

        // 현재 메시지 활동 분석
        MessageTrackingService.MessageAnalysisData analysisData =
                messageTrackingService.getMessageAnalysisData(roomIdx, currentTime);

        HighlightMetrics metrics = new HighlightMetrics(
                analysisData.currentMessageRate,
                analysisData.averageMessageRate,
                analysisData.activeUserCount,
                analysisData.currentMessageRate / Math.max(1.0, analysisData.averageMessageRate)
        );

        log.info("📊 하이라이트 메트릭 - roomIdx: {}, current: {}, avg: {}, users: {}, spike: {}x",
//                log.debug("하이라이트 메트릭 - roomIdx: {}, currentRate: {}, avgRate: {}, activeUsers: {}, spike: {}",
                roomIdx, metrics.currentMessageRate, metrics.averageMessageRate,
                metrics.activeUserCount, metrics.spikeRatio);

        // 하이라이트 조건 검사
        if (shouldCreateHighlight(metrics)) {
            log.info("🔥 스마트 하이라이트 감지! roomIdx={}, 조건: 메시지속도={}(평균: {}), 활성사용자={}, 급증비율={}x",
                    roomIdx, metrics.currentMessageRate, metrics.averageMessageRate,
                    metrics.activeUserCount, String.format("%.1f", metrics.spikeRatio));

            setCooldown(roomIdx);

            // 최근 메시지들 가져오기
            List<String> recentMessages = messageTrackingService.getRecentMessages(roomIdx, 30);

            log.info("📝 최근 메시지 {}개 수집완료", recentMessages.size());

            return Optional.of(new HighlightDetectionResult(metrics, recentMessages));
        } else {
            log.info("❌ 하이라이트 조건 미충족 - roomIdx={}", roomIdx);
            log.info("   - 메시지 수: {} (최소 {})", metrics.currentMessageRate, MIN_MESSAGES_FOR_HIGHLIGHT);
            log.info("   - 급증비율: {}x (최소 {}x)", String.format("%.2f", metrics.spikeRatio), SPIKE_THRESHOLD);
            log.info("   - 활성사용자: {}명 (최소 3명)", metrics.activeUserCount);
        }

        return Optional.empty();
    }

    /**
     * 하이라이트 생성 조건 검사
     */
    private boolean shouldCreateHighlight(HighlightMetrics metrics) {
        // 조건 1: 최소 메시지 수
        if (metrics.currentMessageRate < MIN_MESSAGES_FOR_HIGHLIGHT) {
            return false;
        }

        // 조건 2: 메시지 급증 (평균의 2.5배 이상)
        if (metrics.spikeRatio < SPIKE_THRESHOLD) {
            return false;
        }

        // 조건 3: 최소 3명 이상의 활성 사용자
        if (metrics.activeUserCount < 3) {
            return false;
        }

        return true;
    }

    /**
     * 쿨다운 확인
     */
    private boolean checkCooldown(Long roomIdx) {
        String key = HIGHLIGHT_COOLDOWN_KEY.replace("{roomIdx}", roomIdx.toString());
        return !redisTemplate.hasKey(key);
    }

    /**
     * 쿨다운 설정
     */
    private void setCooldown(Long roomIdx) {
        String key = HIGHLIGHT_COOLDOWN_KEY.replace("{roomIdx}", roomIdx.toString());
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(COOLDOWN_SECONDS));
    }

    // HighlightMetrics DTO
    public static class HighlightMetrics {
        public final double currentMessageRate;
        public final double averageMessageRate;
        public final long activeUserCount;
        public final double spikeRatio;

        public HighlightMetrics(double currentMessageRate, double averageMessageRate,
                                long activeUserCount, double spikeRatio) {
            this.currentMessageRate = currentMessageRate;
            this.averageMessageRate = averageMessageRate;
            this.activeUserCount = activeUserCount;
            this.spikeRatio = spikeRatio;
        }
    }

    // HighlightDetectionResult DTO
    public static class HighlightDetectionResult {
        public final HighlightMetrics metrics;
        public final List<String> recentMessages;

        public HighlightDetectionResult(HighlightMetrics metrics, List<String> recentMessages) {
            this.metrics = metrics;
            this.recentMessages = recentMessages;
        }
    }
}
