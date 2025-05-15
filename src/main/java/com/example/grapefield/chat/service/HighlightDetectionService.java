package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.response.HighlightDetectionResp;
import com.example.grapefield.chat.model.response.HighlightMetrics;
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
    private static final String HIGHLIGHT_LOCK_KEY = "room:{roomIdx}:highlight_lock";
    private static final int LOCK_EXPIRE_SECONDS = 10;

    /**
     * 하이라이트 감지 로직
     */
    public Optional<HighlightDetectionResp> detectHighlight(Long roomIdx, long currentTime) {
        log.info("🔍 하이라이트 감지 시작: roomIdx={}", roomIdx);

        // 1. 분산 락 획득 시도
        if (!acquireLock(roomIdx)) {
            log.info("🔒 다른 인스턴스가 이미 처리 중: roomIdx={}", roomIdx);
            return Optional.empty();
        }

        try {
            // 2. 쿨다운 체크
            if (!checkCooldown(roomIdx)) {
                log.info("❄️ 쿨다운 중이라 스킵: roomIdx={}", roomIdx);
                return Optional.empty();
            }

            // 3. 현재 메시지 활동 분석
            MessageTrackingService.MessageAnalysisData analysisData =
                    messageTrackingService.getMessageAnalysisData(roomIdx, currentTime);

            HighlightMetrics metrics = new HighlightMetrics(
                    analysisData.currentMessageRate,
                    analysisData.averageMessageRate,
                    analysisData.activeUserCount,
                    analysisData.currentMessageRate / Math.max(1.0, analysisData.averageMessageRate)
            );

            log.info("📊 하이라이트 메트릭 - roomIdx: {}, current: {}, avg: {}, users: {}, spike: {}x",
                    roomIdx, metrics.getCurrentMessageRate(), metrics.getAverageMessageRate(),
                    metrics.getActiveUserCount(), metrics.getSpikeRatio());

            // 4. 하이라이트 조건 검사
            if (shouldCreateHighlight(metrics)) {
                log.info("🔥 스마트 하이라이트 감지! roomIdx={}, 조건: 메시지속도={}(평균: {}), 활성사용자={}, 급증비율={}x",
                        roomIdx, metrics.getCurrentMessageRate(), metrics.getAverageMessageRate(),
                        metrics.getActiveUserCount(), String.format("%.1f", metrics.getSpikeRatio()));

                setCooldown(roomIdx);

                // 최근 메시지들 가져오기
                List<String> recentMessages = messageTrackingService.getRecentMessages(roomIdx, 30);

                log.info("📝 최근 메시지 {}개 수집완료", recentMessages.size());

                return Optional.of(new HighlightDetectionResp(metrics, recentMessages));
            } else {
                log.info("❌ 하이라이트 조건 미충족 - roomIdx={}", roomIdx);
                log.info("   - 메시지 수: {} (최소 {})", metrics.getCurrentMessageRate(), MIN_MESSAGES_FOR_HIGHLIGHT);
                log.info("   - 급증비율: {}x (최소 {}x)", String.format("%.2f", metrics.getSpikeRatio()), SPIKE_THRESHOLD);
                log.info("   - 활성사용자: {}명 (최소 3명)", metrics.getActiveUserCount());
            }

            return Optional.empty();

        } finally {
            // 5. 락 해제
            releaseLock(roomIdx);
        }
    }

    /**
     * Redis 분산 락 획득
     */
    private boolean acquireLock(Long roomIdx) {
        String lockKey = HIGHLIGHT_LOCK_KEY.replace("{roomIdx}", roomIdx.toString());

        // 현재 인스턴스 식별을 위한 값 생성
        String instanceId = Thread.currentThread().getName() + "-" + System.currentTimeMillis();

        // SET key value NX EX seconds (원자적 연산)
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(
                lockKey,
                instanceId,
                Duration.ofSeconds(LOCK_EXPIRE_SECONDS)
        );

        if (Boolean.TRUE.equals(locked)) {
            log.info("🔐 락 획득 성공: roomIdx={}, instance={}", roomIdx, instanceId);
            return true;
        } else {
            log.info("🔒 락 획득 실패: roomIdx={} (다른 인스턴스가 처리 중)", roomIdx);
            return false;
        }
    }

    /**
     * Redis 분산 락 해제
     */
    private void releaseLock(Long roomIdx) {
        String lockKey = HIGHLIGHT_LOCK_KEY.replace("{roomIdx}", roomIdx.toString());
        try {
            redisTemplate.delete(lockKey);
            log.info("🔓 락 해제 완료: roomIdx={}", roomIdx);
        } catch (Exception e) {
            log.error("락 해제 중 오류 발생: roomIdx={}", roomIdx, e);
        }
    }

    /**
     * 하이라이트 생성 조건 검사
     */
    private boolean shouldCreateHighlight(HighlightMetrics metrics) {
        // 조건 1: 최소 메시지 수
        if (metrics.getCurrentMessageRate() < MIN_MESSAGES_FOR_HIGHLIGHT) {
            return false;
        }

        // 조건 2: 메시지 급증 (평균의 2.5배 이상)
        if (metrics.getSpikeRatio() < SPIKE_THRESHOLD) {
            return false;
        }

        // 조건 3: 최소 3명 이상의 활성 사용자
        if (metrics.getActiveUserCount() < 3) {
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
}