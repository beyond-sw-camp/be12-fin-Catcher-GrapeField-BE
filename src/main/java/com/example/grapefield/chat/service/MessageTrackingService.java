package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.request.ChatMessageKafkaReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTrackingService {

    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis key 패턴
    private static final String ROOM_MESSAGE_COUNT_KEY = "room:{roomIdx}:message_count";
    private static final String ROOM_MESSAGES_KEY = "room:{roomIdx}:messages";
    private static final String ROOM_ACTIVE_USERS_KEY = "room:{roomIdx}:active_users";

    // 설정
    private static final int BASELINE_WINDOW_MINUTES = 10;

    /**
     * 메시지 추적 및 저장
     */
    public void trackMessage(ChatMessageKafkaReq kafkaReq) {
        Long roomIdx = kafkaReq.getRoomIdx();
        long currentTime = System.currentTimeMillis();

        log.info("📊 메시지 추적 시작: roomIdx={}, userIdx={}, content={}",
                roomIdx, kafkaReq.getSendUserIdx(), kafkaReq.getContent());

        try {
            // 1. 시간대별 메시지 수 증가
            log.info("1️⃣ updateMessageCount 호출");
            updateMessageCount(roomIdx, currentTime);
            log.info("✅ updateMessageCount 완료");

            // 2. 활성 사용자 추가
            log.info("2️⃣ updateActiveUsers 호출");
            updateActiveUsers(roomIdx, kafkaReq.getSendUserIdx(), currentTime);
            log.info("✅ updateActiveUsers 완료");

            // 3. 메시지 내용 저장 (최근 100개만 유지)
            log.info("3️⃣ storeRecentMessage 호출");
            storeRecentMessage(roomIdx, kafkaReq);
            log.info("✅ storeRecentMessage 완료");

        } catch (Exception e) {
            log.error("💥 메시지 추적 중 오류 발생", e);
            throw e; // 오류를 다시 던져서 상위에서 확인
        }

        log.info("✅ 메시지 추적 완료: roomIdx={}", roomIdx);
    }

    /**
     * 시간대별 메시지 수를 Redis ZSet으로 관리
     */
    private void updateMessageCount(Long roomIdx, long currentTime) {
        String key = ROOM_MESSAGE_COUNT_KEY.replace("{roomIdx}", roomIdx.toString());

        log.info("🔢 메시지 수 업데이트 시작: key={}", key);

        try {
            // 1분 단위로 집계 (timestamp를 1분 단위로 반올림)
            long minuteTimestamp = (currentTime / 60000) * 60000;

            log.info("📅 타임스탬프: {}, 분 단위: {}", currentTime, minuteTimestamp);

            // ZSet에 해당 분의 메시지 수 증가
            Double score = redisTemplate.opsForZSet().incrementScore(key, minuteTimestamp, 1);
            log.info("✅ ZSet 업데이트 완료: key={}, score={}", key, score);

            // 삭제 로직을 더 안전하게 변경 - 현재 분 기준으로 10분 이전만 삭제
            long cutoffTime = minuteTimestamp - (BASELINE_WINDOW_MINUTES * 60000);

            // 현재 저장된 모든 타임스탬프 조회
            Set<Object> allMembers = redisTemplate.opsForZSet().range(key, 0, -1);
            log.info("📋 전체 저장된 데이터: {}", allMembers);

            // cutoff 시간보다 오래된 것만 삭제
            int removedCount = 0;
            for (Object member : allMembers) {
                if (member instanceof Long) {
                    Long timestamp = (Long) member;
                    if (timestamp < cutoffTime) {
                        redisTemplate.opsForZSet().remove(key, timestamp);
                        removedCount++;
                        log.info("🗑️ 삭제: {}", timestamp);
                    }
                }
            }

            log.info("🗑️ 정확한 삭제 완료: removed={}, cutoff={}", removedCount, cutoffTime);

            // TTL 설정
            Boolean expire = redisTemplate.expire(key, Duration.ofHours(1));
            log.info("⏰ TTL 설정: success={}", expire);

            // 최종 확인
            Set<Object> finalEntries = redisTemplate.opsForZSet().range(key, 0, -1);
            log.info("📊 최종 ZSet 내용: {}", finalEntries);

        } catch (Exception e) {
            log.error("💥 메시지 수 업데이트 실패: key={}", key, e);
            throw e;
        }
    }

    /**
     * 활성 사용자 추적
     */
    private void updateActiveUsers(Long roomIdx, Long senderIdx, long currentTime) {
        String key = ROOM_ACTIVE_USERS_KEY.replace("{roomIdx}", roomIdx.toString());

        // 사용자별 마지막 활동 시간 저장
        redisTemplate.opsForZSet().add(key, senderIdx, currentTime);

        // 5분 이전에 활동한 사용자는 제거
        long cutoff = currentTime - (5 * 60000);
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, cutoff);

        redisTemplate.expire(key, Duration.ofMinutes(30));
    }

    /**
     * 최근 메시지 저장 (String으로 직접 저장)
     */
    private void storeRecentMessage(Long roomIdx, ChatMessageKafkaReq kafkaReq) {
        String key = ROOM_MESSAGES_KEY.replace("{roomIdx}", roomIdx.toString());

        try {
            // 메시지 내용만 String으로 저장 (JSON 사용하지 않음)
            String messageContent = kafkaReq.getContent();

            redisTemplate.opsForList().rightPush(key, messageContent);

            log.debug("💬 메시지 저장: roomIdx={}, content={}, key={}",
                    roomIdx, messageContent, key);

            // 최근 100개만 유지
            redisTemplate.opsForList().trim(key, -100, -1);
            redisTemplate.expire(key, Duration.ofHours(2));

        } catch (Exception e) {
            log.error("💥 메시지 저장 중 오류 발생: roomIdx={}", roomIdx, e);
            throw e;
        }
    }

    /**
     * 최근 메시지들 가져오기
     */
    public List<String> getRecentMessages(Long roomIdx, int count) {
        String key = ROOM_MESSAGES_KEY.replace("{roomIdx}", roomIdx.toString());

        try {
            List<Object> recentMessages = redisTemplate.opsForList().range(key, -count, -1);

            return recentMessages.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("💥 최근 메시지 조회 중 오류 발생: roomIdx={}", roomIdx, e);
            return Collections.emptyList(); // 오류 발생시 빈 리스트 반환
        }
    }

    /**
     * 메시지 수 분석 데이터 가져오기
     */
    public MessageAnalysisData getMessageAnalysisData(Long roomIdx, long currentTime) {
        String messageCountKey = ROOM_MESSAGE_COUNT_KEY.replace("{roomIdx}", roomIdx.toString());
        String activeUsersKey = ROOM_ACTIVE_USERS_KEY.replace("{roomIdx}", roomIdx.toString());

        log.info("🔍 메시지 분석 시작: roomIdx={}, currentTime={}", roomIdx, currentTime);

        // 현재 시간을 1분 단위로 반올림
        long currentMinute = (currentTime / 60000) * 60000;
        log.info("⏰ 현재 분 단위 시간: {}", currentMinute);

        // 최근 2분간의 메시지 수
        long recentStart = currentMinute - (2 * 60000);
        long recentEnd = currentMinute + 60000;

        log.info("🔍 조회 범위: {} ~ {}", recentStart, recentEnd);

        // ZSet의 모든 데이터 조회 후 필터링
        Set<ZSetOperations.TypedTuple<Object>> allData = redisTemplate.opsForZSet()
                .rangeWithScores(messageCountKey, 0, -1);

        log.info("📋 전체 데이터: {}",
                allData.stream()
                        .map(tuple -> tuple.getValue() + ":" + tuple.getScore())
                        .collect(Collectors.toList()));

        // 최근 2분간 데이터 필터링
        double currentMessageRate = allData.stream()
                .filter(tuple -> {
                    if (tuple.getValue() instanceof Long) {
                        Long timestamp = (Long) tuple.getValue();
                        return timestamp >= recentStart && timestamp <= recentEnd;
                    }
                    return false;
                })
                .mapToDouble(ZSetOperations.TypedTuple::getScore)
                .sum();

        log.info("📊 최근 2분간 메시지: {}", currentMessageRate);

        // 지난 10분간의 평균 계산
        long baselineStart = currentMinute - (BASELINE_WINDOW_MINUTES * 60000);
        double totalBaseline = allData.stream()
                .filter(tuple -> {
                    if (tuple.getValue() instanceof Long) {
                        Long timestamp = (Long) tuple.getValue();
                        return timestamp >= baselineStart && timestamp < recentStart;
                    }
                    return false;
                })
                .mapToDouble(ZSetOperations.TypedTuple::getScore)
                .sum();

        long baselineCount = allData.stream()
                .filter(tuple -> {
                    if (tuple.getValue() instanceof Long) {
                        Long timestamp = (Long) tuple.getValue();
                        return timestamp >= baselineStart && timestamp < recentStart;
                    }
                    return false;
                })
                .mapToLong(tuple -> 1)
                .sum();

        double averageMessageRate = baselineCount == 0 ? 1.0 :
                totalBaseline / Math.max(1, baselineCount);

        log.info("📊 평균 계산: total={}, entries={}, avg={}",
                totalBaseline, baselineCount, averageMessageRate);

        // 현재 활성 사용자 수
        Long activeUserCount = redisTemplate.opsForZSet()
                .count(activeUsersKey, currentTime - (5 * 60000), currentTime);

        log.info("👥 활성 사용자: {}명", activeUserCount);

        return new MessageAnalysisData(currentMessageRate, averageMessageRate, activeUserCount);
    }

    // MessageAnalysisData DTO
    public static class MessageAnalysisData {
        public final double currentMessageRate;
        public final double averageMessageRate;
        public final long activeUserCount;

        public MessageAnalysisData(double currentMessageRate, double averageMessageRate, long activeUserCount) {
            this.currentMessageRate = currentMessageRate;
            this.averageMessageRate = averageMessageRate;
            this.activeUserCount = activeUserCount;
        }
    }
}
