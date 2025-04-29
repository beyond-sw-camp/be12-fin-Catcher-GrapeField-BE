package com.example.grapefield.events.repository;

import com.example.grapefield.events.model.entity.EventCategory;
import com.example.grapefield.events.model.entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventsRepository extends JpaRepository<Events, Long>, EventsCustomRepository {
  List<Events> findByTitle(String title);
  List<Events> findByCategory(EventCategory category);
}