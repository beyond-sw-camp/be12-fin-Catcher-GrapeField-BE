package com.example.demo.events.participant.model.entity;

import com.example.demo.events.model.entity.Events;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventsCast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "events_idx")
    private Events events;

    @ManyToOne
    @JoinColumn(name = "performer_idx")
    private Performer performer;

    @ManyToOne
    @JoinColumn(name = "organization_idx")
    private Organization organization;

    private String role;
    private Integer castOrder;
    private String description;
}
