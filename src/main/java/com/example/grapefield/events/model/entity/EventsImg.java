package com.example.grapefield.events.model.entity;

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
public class EventsImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String imgUrl;
    private Long displayOrder;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "events_idx")
    private Events events;

}