package com.example.grapefield.events.review.model.entity;

import com.example.grapefield.events.model.entity.Events;
import com.example.grapefield.user.model.entity.User;
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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "events_idx")
    private Events events;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    private Long rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private Boolean isVisible = true;
}