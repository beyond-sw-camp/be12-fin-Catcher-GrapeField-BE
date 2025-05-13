package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatMessageCurrent;
import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.repository.ChatMessageCurrentRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageCurrentRepository chatMessageCurrentRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ChatRoom findByIdx(Long roomIdx) {
        return chatRoomRepository.findById(roomIdx)
                .orElseThrow(()->
                        new NoSuchElementException("해당 채팅방이 존재하지 않습니다. roomIdx: " + roomIdx));
    }

    /**
     * roomIdx 방의 메시지를 페이징하여 조회한다.
     * @param roomIdx 조회할 채팅방 ID
     * @param page    0부터 시작하는 페이지 번호
     * @param size    한 페이지당 메시지 수
     */
    public Page<ChatMessageCurrent> getPaginatedMessages(Long roomIdx, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return chatMessageCurrentRepository.findByChatRoom_IdxOrderByCreatedAtDesc(roomIdx, pageable);
    }


    @Transactional
    public void increaseHeartCount(Long roomIdx) {
        // DB 갱신
        ChatRoom chatRoom = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음. roomIdx=" + roomIdx));
        log.info("♥️하트 개수 갱신 전!! 🌟chatRoom.getHeartCnt:"+chatRoom.getHeartCnt());
        chatRoom.increaseHeart(); // heartCnt += 1
        log.info("✅[DataBase] ChatRoom({}) ♥️하트 개수 갱신 heartCnt updated: {}", roomIdx, chatRoom.getHeartCnt());
        log.info("♥️하트 개수 갱신 후!! 🌟chatRoom.getHeartCnt:"+chatRoom.getHeartCnt());
        // Redis 캐시에 동기화
        log.info("🌟🌟🌟🌟🌟🌟 Redis 코드 시작... ");
        String redisKey = "chat:"+roomIdx+":likes";
        log.info("🌟호출 당시 redisKey:"+redisKey);
        Long newCount = redisTemplate.opsForValue().increment(redisKey);
        log.info("🌟호출 당시 redisKey:"+redisKey);
        log.info("🌟호출 당시 newCount:"+newCount);
        if (newCount == null) {
            log.info("🌟newCount = null");
            //키가 없을 경우 DB의 값으로 초기값 세팅
            redisTemplate.opsForValue().set(redisKey, chatRoom.getHeartCnt());
            log.info("🌟null일 때 chatRoom.getHeartCnt()로 set 하고 나서 redisKey:"+redisKey);
            log.info("🌟chatRoom.getHeartCnt:"+chatRoom.getHeartCnt());
            newCount = chatRoom.getHeartCnt();
            log.info("🌟null일 때 chatRoom.getHeartCnt()로 할당하고 나서 newCount:"+newCount);
        }
        log.info("✅[Redis] ChatRoom({}) ♥️하트 개수 갱신 heartCnt updated: {}", roomIdx, newCount);
    }

}
