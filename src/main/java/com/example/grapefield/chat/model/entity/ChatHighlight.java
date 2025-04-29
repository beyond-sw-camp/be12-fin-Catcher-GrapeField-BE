package com.example.grapefield.chat.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatHighlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "room_idx")
    private ChatRoom chatRoom;


    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private Long messageCnt;

    /*
    optional = false
    - JPA 수준에서 "반드시 값이 있어야 한다"고 선언
    fetch = FetchType.LAZY
    - 불필요한 조인 없이 필요할 때만 메세지를 로딩하도록 함

    @JoinColumn(nullable = false)
    - 데이터베이스 컬럼에도 NOT NULL 제약을 걸어 삽입 시점에 오류가 나도록 강제
    * */
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "message_idx", unique = true, nullable = false)
    private ChatMessageBase message;
}
