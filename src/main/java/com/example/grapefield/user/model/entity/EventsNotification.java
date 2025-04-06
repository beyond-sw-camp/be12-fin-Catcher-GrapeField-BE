package com.example.grapefield.user.model.entity;

import com.example.grapefield.events.model.entity.Events;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventsNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_idx")
    private Events events;

    private Boolean isRead;

    private LocalDateTime notificationTime;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}