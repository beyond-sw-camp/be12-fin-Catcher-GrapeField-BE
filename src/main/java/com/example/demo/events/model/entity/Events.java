package com.example.demo.events.model.entity;

import com.example.demo.events.chat.model.entity.ChatRoom;
import com.example.demo.events.participant.model.entity.EventsCast;
import com.example.demo.events.participant.model.entity.EventsParticipation;
import com.example.demo.events.post.model.entity.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Enumerated(EnumType.STRING)
    private EventCategory category; // 뮤지컬, 전시 등
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String posterImgUrl;
    private String description;
    private String notification; // 관람시 안내 사항
    private String venue;
    private Integer runningTime; // 분 단위
    @Enumerated(EnumType.STRING)
    private AgeLimit ageLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "events")
    private List<EventsImg> eventsImgList;

    @OneToMany(mappedBy = "events")
    private List<TicketInfo> ticketInfoList;

    @OneToMany(mappedBy = "events")
    private List<SeatPrice> seatPriceList;

    @OneToMany(mappedBy = "events")
    private List<Review> reviewList;

    @OneToMany(mappedBy = "events")
    private List<EventsCast> eventsCastList;

    @OneToMany(mappedBy = "events")
    private List<EventsParticipation> eventsParticipationList;

    @OneToOne(mappedBy = "events")
    private Board board;

    @OneToOne(mappedBy = "events")
    private ChatRoom chatRoom;
}

