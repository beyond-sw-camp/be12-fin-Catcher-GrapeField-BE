package com.example.demo.events.participant.model.entity;

import com.example.demo.events.model.entity.Events;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.AssociationType;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventsParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "event_idx")
    private Events events;

    @ManyToOne
    @JoinColumn(name = "organization_idx")
    private Organization organization;

    @Enumerated(EnumType.STRING)
    private AssociationType associationType;
}
