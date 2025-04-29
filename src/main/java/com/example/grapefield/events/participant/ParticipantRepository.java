package com.example.grapefield.events.participant;

import com.example.grapefield.events.model.entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Events, Long> {
}
