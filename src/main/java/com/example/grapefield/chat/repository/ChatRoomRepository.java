package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.response.PopularChatRoomListResp;
import com.example.grapefield.chat.repository.custom.ChatRoomRepositoryCustom;
import com.example.grapefield.events.model.entity.EventCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {

    @Query("SELECT c.idx from ChatRoom c")
    List<Long> findAllChatRoomsByIdx();

    @EntityGraph(attributePaths = {"events"})
    @Query("SELECT c FROM ChatRoom c")
    Slice<ChatRoom> findAllWithEventsSlice(Pageable pageable);

    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.events WHERE c.events.category IN :categories")
    Slice<ChatRoom> findChatRoomsByCategoryInSlice(@Param("categories") List<EventCategory> categories, Pageable pageable);

    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.events")
    List<ChatRoom> findAllWithEvents();


    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.events e LEFT JOIN c.memberList m GROUP BY c ORDER BY c.heartCnt DESC")
    List<ChatRoom> findTop10ByHeartCnt(Pageable pageable);

    @Query("""
    SELECT c FROM ChatRoom c 
    JOIN FETCH c.events e 
    LEFT JOIN c.memberList m 
    WHERE e.endDate > CURRENT_DATE 
    ORDER BY e.startDate DESC, c.heartCnt DESC
    """)
    List<ChatRoom> findTop10ByRecentEventsAndHeartCnt(Pageable pageable);

}
