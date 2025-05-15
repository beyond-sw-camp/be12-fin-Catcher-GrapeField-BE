package com.example.grapefield.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Redis 상태 확인용 임시 컨트롤러
@RestController
@RequestMapping("/api/admin/redis")
@RequiredArgsConstructor
public class RedisStatusController {

    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/room/{roomIdx}/status")
    public Map<String, Object> getRoomRedisStatus(@PathVariable Long roomIdx) {
        Map<String, Object> status = new HashMap<>();

        // 메시지 수 조회
        String messageCountKey = "room:" + roomIdx + ":message_count";
        Set<Object> messageCounts = redisTemplate.opsForZSet().range(messageCountKey, 0, -1);
        status.put("messageCountEntries", messageCounts.size());

        // 활성 사용자 수 조회
        String activeUsersKey = "room:" + roomIdx + ":active_users";
        Long activeUserCount = redisTemplate.opsForZSet().zCard(activeUsersKey);
        status.put("activeUserCount", activeUserCount);

        // 최근 메시지 수 조회
        String messagesKey = "room:" + roomIdx + ":messages";
        Long messageCount = redisTemplate.opsForList().size(messagesKey);
        status.put("recentMessageCount", messageCount);

        // 쿨다운 상태 확인
        String cooldownKey = "room:" + roomIdx + ":highlight_cooldown";
        Boolean hasCooldown = redisTemplate.hasKey(cooldownKey);
        status.put("inCooldown", hasCooldown);

        return status;
    }

    @GetMapping("/room/{roomIdx}/recent-messages")
    public List<Object> getRecentMessages(@PathVariable Long roomIdx) {
        String messagesKey = "room:" + roomIdx + ":messages";
        return redisTemplate.opsForList().range(messagesKey, -10, -1);
    }
}
