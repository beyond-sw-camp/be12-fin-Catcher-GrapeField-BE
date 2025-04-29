package com.example.grapefield.chat.repository.custom;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.events.model.entity.EventCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatRoomRepositoryCustom {
    Slice<ChatRoom> findAllWithEvents(Pageable pageable);
    Slice<ChatRoom> findAllOrderByHeartCnt(Pageable pageable);

    // 카테고리 필터링
    Slice<ChatRoom> findChatRoomsByCategory(EventCategory category, Pageable pageable);

}
