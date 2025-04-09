package com.example.grapefield.events;

import com.example.grapefield.events.model.entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsRepository extends JpaRepository<Events, Long> {
}
