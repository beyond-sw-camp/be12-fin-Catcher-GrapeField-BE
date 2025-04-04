package com.example.demo.user.model.entity;

import com.example.demo.events.model.entity.Events;
import com.example.demo.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//같은 공연(이벤트)을 두 번 즐겨찾기하거나, 두 번 캘린더 등록 방지
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_idx", "events_idx"})
)
public class EventsInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_idx")
    private Events events;

    private Boolean isFavorite;
    private Boolean isNotify;
    private Boolean isCalendar;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
