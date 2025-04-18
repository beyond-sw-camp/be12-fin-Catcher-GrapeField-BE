package com.example.grapefield.chat.service;

import com.example.grapefield.chat.model.entity.ChatRoom;
import com.example.grapefield.chat.model.entity.ChatroomMember;
import com.example.grapefield.chat.repository.ChatRoomMemberRepository;
import com.example.grapefield.chat.repository.ChatRoomRepository;
import com.example.grapefield.user.model.entity.User;
import com.example.grapefield.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomMemberService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomMemberRepository memberRepository;

    @Transactional
    public void joinRoom(Long userIdx, Long roomIdx) {
        ChatRoom room = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        boolean exists = memberRepository.existsByChatRoomAndUser(room, user);

        if (!exists) {
            ChatroomMember newMember = ChatroomMember.builder()
                    .chatRoom(room)
                    .user(user)
                    .lastActiveAt(LocalDateTime.now())
                    .lastReadAt(null)
                    .mute(false)
                    .build();
            memberRepository.save(newMember);
            log.info("🆕 최초입장 → ChatroomMember 저장됨");
        } else {
            log.info("✅ 이미 참여 중 → 아무 처리 안함");
        }
    }

    // 특정 채팅방의 참여자 수
    public Map<Long, Integer> getParticipantCountMap() {
        return memberRepository.countParticipantsGroupedByRoom().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }

    // 채팅방 퇴장
    @Transactional
    public void leaveRoom(Long userIdx, Long roomIdx) {
        ChatRoom room = chatRoomRepository.findById(roomIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        ChatroomMember member = memberRepository.findByChatRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalStateException("이미 퇴장한 채팅방입니다."));

        memberRepository.delete(member);
        log.info("👋 퇴장 완료 - userIdx: {}, roomIdx: {}", userIdx, roomIdx);
    }

}
