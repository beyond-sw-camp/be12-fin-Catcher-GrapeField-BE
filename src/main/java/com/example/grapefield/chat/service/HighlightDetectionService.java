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
//    private static final double SPIKE_THRESHOLD = 2.5;
//    private static final int MIN_MESSAGES_FOR_HIGHLIGHT = 15;
//    private static final int COOLDOWN_SECONDS = 300;  // 5분, 하이라이트 생성 후 쿨다운
    private static final int MIN_ACTIVE_USERS = 1;

    // 테스트용으로 임계값 낮춤 // 더 낮춰서 테스트해도 됨.
    private static final double SPIKE_THRESHOLD = 0.1; // 2.5 → 1.2로 낮춤
    private static final int MIN_MESSAGES_FOR_HIGHLIGHT = 15; // 15 → 10으로 낮춤
    private static final int COOLDOWN_SECONDS = 30; // 120 → 30초로 단축

    // 시간당 제한 추가
    private static final int MAX_HIGHLIGHTS_PER_HOUR = 100; // 💬🫱🏻(Hyorim K) 더 늘리고 싶습니다
    private static final String HIGHLIGHT_COOLDOWN_KEY = "room:{roomIdx}:highlight_cooldown";
    private static final String HIGHLIGHT_LOCK_KEY = "room:{roomIdx}:highlight_lock";
    private static final int LOCK_EXPIRE_SECONDS = 10;
    private static final String HOURLY_HIGHLIGHTS_KEY = "room:{roomIdx}:hourly_highlights";

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

            // 🆕 3. 시간당 제한 체크
            if (!checkHourlyLimit(roomIdx)) {
                log.info("📈 시간당 하이라이트 제한 도달: roomIdx={}", roomIdx);
                return Optional.empty();
            }

            // 4. 현재 메시지 활동 분석 (기존 코드와 동일)
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

            // 5. 하이라이트 조건 검사
            if (shouldCreateHighlight(metrics)) {
                log.info("🔥 스마트 하이라이트 감지! roomIdx={}", roomIdx);
                setCooldown(roomIdx);

                // 🆕 시간당 카운트 증가
                incrementHourlyCount(roomIdx);

                List<String> recentMessages = messageTrackingService.getRecentMessages(roomIdx, 30);
                return Optional.of(new HighlightDetectionResp(metrics, recentMessages));
            } else {
                log.info("❌ 하이라이트 조건 미충족 - roomIdx={}", roomIdx);
                log.info("   - 메시지 수: {} (최소 {})", metrics.getCurrentMessageRate(), MIN_MESSAGES_FOR_HIGHLIGHT);
                log.info("   - 급증비율: {}x (최소 {}x)", String.format("%.2f", metrics.getSpikeRatio()), SPIKE_THRESHOLD);
                log.info("   - 활성사용자: {}명 (최소 {}명)", metrics.getActiveUserCount(), MIN_ACTIVE_USERS);
            }

            return Optional.empty();

        } finally {
            // 6. 락 해제
            releaseLock(roomIdx);
        }
    }

    /**
     * 🆕 시간당 하이라이트 제한 체크
     */
    private boolean checkHourlyLimit(Long roomIdx) {
        String key = HOURLY_HIGHLIGHTS_KEY.replace("{roomIdx}", roomIdx.toString());
        long currentTime = System.currentTimeMillis();
        long oneHourAgo = currentTime - 3600000; // 1시간 전

        // 1시간 내 하이라이트 개수 조회
        long count = redisTemplate.opsForZSet().count(key, oneHourAgo, currentTime);

        log.info("📊 시간당 하이라이트 체크: roomIdx={}, 현재 개수={}, 제한={}",
                roomIdx, count, MAX_HIGHLIGHTS_PER_HOUR);

        return count < MAX_HIGHLIGHTS_PER_HOUR;
    }

    /**
     * 🆕 시간당 하이라이트 카운트 증가
     */
    private void incrementHourlyCount(Long roomIdx) {
        String key = HOURLY_HIGHLIGHTS_KEY.replace("{roomIdx}", roomIdx.toString());
        long currentTime = System.currentTimeMillis();

        // 현재 시간을 ZSet에 추가
        redisTemplate.opsForZSet().add(key, currentTime, currentTime);

        // 1시간 이전 데이터 정리
        long oneHourAgo = currentTime - 3600000;
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, oneHourAgo);

        // TTL 설정 (2시간)
        redisTemplate.expire(key, Duration.ofHours(2));

        log.info("📈 시간당 카운트 증가: roomIdx={}", roomIdx);
    }


    /**
     * Redis 분산 락 획득
     */
    private boolean acquireLock(Long roomIdx) {
        String lockKey = HIGHLIGHT_LOCK_KEY.replace("{roomIdx}", roomIdx.toString());
        String instanceId = Thread.currentThread().getName() + "-" + System.currentTimeMillis();

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(
                lockKey, instanceId, Duration.ofSeconds(LOCK_EXPIRE_SECONDS));

        if (Boolean.TRUE.equals(locked)) {
            log.info("🔐 락 획득 성공: roomIdx={}, instance={}", roomIdx, instanceId);
            return true;
        } else {
            log.info("🔒 락 획득 실패: roomIdx={}", roomIdx);
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
        if (metrics.getCurrentMessageRate() < MIN_MESSAGES_FOR_HIGHLIGHT) {
            return false;
        }
        if (metrics.getSpikeRatio() < SPIKE_THRESHOLD) {
            return false;
        }
        if (metrics.getActiveUserCount() < MIN_ACTIVE_USERS) {
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