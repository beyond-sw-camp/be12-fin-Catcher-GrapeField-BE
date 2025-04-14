package com.example.grapefield.chat.repository;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.ChatroomMember;
import com.example.grapefield.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ChatRoomMemberRepository extends JpaRepository<ChatroomMember, Long> {
    // 유저가 해당 채팅방에 이미 참여한 적이 있는지 확인
    Optional<ChatroomMember> findByChatRoomAndUser(ChatRoom chatRoom, User user);
}
