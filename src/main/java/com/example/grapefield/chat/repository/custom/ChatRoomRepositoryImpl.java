package com.example.grapefield.chat.repository.custom;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.QChatRoom;
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
}
