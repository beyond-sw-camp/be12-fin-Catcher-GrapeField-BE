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
import java.util.Objects;

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
    public Long increaseHeart(Long roomIdx) {
        // DB 갱신
        ChatRoom chatRoom = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음. roomIdx=" + roomIdx));

        String redisKey = "chat:"+roomIdx+":likes";
        Object rawValue = redisTemplate.opsForValue().get(redisKey);

        Long newCount;
        if (rawValue == null) {
            newCount = null;
            log.info("🌟rawValue==null 일 때 newCount: {}", newCount);
        }
        else {
            newCount = Long.parseLong((redisTemplate.opsForValue().get(redisKey)).toString());
            log.info("🌟rawValue!=null 일 때 newCount: {}", newCount);
        }
        if (!Objects.equals(newCount, chatRoom.getHeartCnt()) || newCount == null){
            chatRoom.increaseHeart(); // heartCnt += 1
            log.info("✅[DataBase] ChatRoom({}) ♥️하트 개수 갱신 heartCnt:{}", roomIdx, chatRoom.getHeartCnt());
            redisTemplate.opsForValue().set(redisKey, chatRoom.getHeartCnt());
            // newCount = chatRoom.getHeartCnt();
            newCount = Long.parseLong((redisTemplate.opsForValue().get(redisKey)).toString());
            log.info("⭐redisTemplate.opsForValue().set(redisKey, chatRoom.getHeartCnt()); chatRoom.getHearCnt() = " + chatRoom.getHeartCnt());
            log.info("⭐redisTemplate.opsForValue().set(redisKey, chatRoom.getHeartCnt()); newCount"+ newCount);
            log.info("✅[Redis] chat:{}:likes ♥️하트 개수 갱신 heartCnt:{}", roomIdx,newCount);
        } else {
            chatRoom.increaseHeart(); // heartCnt += 1
            log.info("✅[DataBase] ChatRoom({}) ♥️하트 개수 갱신 heartCnt:{}", roomIdx, chatRoom.getHeartCnt());
            newCount = redisTemplate.opsForValue().increment(redisKey);
            log.info("✅[Redis] chat:{}:likes ♥️하트 개수 갱신 heartCnt:{}", roomIdx,newCount);
        }
        return newCount;

    }

    @Transactional
    public Long increaseHeartRedis(ChatRoom chatRoom) {
        Long roomIdx = chatRoom.getIdx();
        String redisKey = "chat:"+roomIdx+":likes";
        Long newCount = redisTemplate.opsForValue().increment(redisKey);
        log.info("⭐Long newCount = redisTemplate.opsForValue().increment(redisKey); 후 chatRoom.getHearCnt() = " + chatRoom.getHeartCnt());
        log.info("⭐Long newCount = redisTemplate.opsForValue().increment(redisKey); 후 newCount = " + newCount);

        return newCount;

    }

}
