package com.example.grapefield.chat.repository.custom;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.QChatRoom;
import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.QEvents;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 전체 채팅방 목록(roomIdx asc 순서)
    @Override
    public Slice<ChatRoom> findAllWithEvents(Pageable pageable) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QEvents events = QEvents.events;

        List<ChatRoom> content = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.events, events).fetchJoin()
                .offset(pageable.getOffset())
                .orderBy(chatRoom.idx.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.remove(content.size() - 1);

        return new SliceImpl<>(content, pageable, hasNext);
    }

    // 전체 인기 채팅방 순서로 목록(heartCnt desc 순서)
    @Override
    public Slice<ChatRoom> findAllOrderByHeartCnt(Pageable pageable) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QEvents events = QEvents.events;

        List<ChatRoom> content = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.events, events).fetchJoin()
                .orderBy(chatRoom.heartCnt.desc())  // 💖 하트순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.remove(content.size() - 1);

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<ChatRoom> findChatRoomsByCategory(EventCategory category, Pageable pageable) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QEvents events = QEvents.events;

        List<ChatRoom> content = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.events, events).fetchJoin()
                .where(events.category.eq(category))
                .orderBy(chatRoom.idx.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.remove(content.size() - 1);

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
