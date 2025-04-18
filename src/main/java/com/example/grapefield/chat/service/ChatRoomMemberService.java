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

        memberRepository.findByChatRoomAndUser(room, user)
                .ifPresentOrElse(
                        member -> {
                            member.updateLastActiveAt(LocalDateTime.now());
                            log.info("✅ 재입장 → lastActiveAt 갱신됨");
                        },
                        () -> {
                            ChatroomMember newMember = ChatroomMember.builder()
                                    .chatRoom(room)
                                    .user(user)
                                    .lastActiveAt(LocalDateTime.now())
                                    .lastReadAt(null)
                                    .mute(false)
                                    .build();
                            memberRepository.save(newMember);
                            log.info("🆕 최초입장 → ChatroomMember 저장됨");
                        }
                );
    }
    // 특정 채팅방의 참여자 수
    public Map<Long, Integer> getParticipantCountMap() {
        return memberRepository.countParticipantsGroupedByRoom().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }
}
